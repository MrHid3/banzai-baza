<script lang="ts">
    import type { PageData } from './$types';

    // ── Types ────────────────────────────────────────────────────────────────

    interface PaymentLocation {
        id: number;
        name: string;
        shortname: string;
        isActive: boolean;
    }

    interface PayerIn {
        uuid: string;
        email: string;
    }

    interface Payment {
        uuid: string;
        amount: number;
        month: string | null;
        location: PaymentLocation;
        paymentMethod: string;
        paymentType: string;
        timeStamp: string;
        payerIn: PayerIn;
    }

    // ── Constants ────────────────────────────────────────────────────────────

    const ENTRY_FEE_TYPE = 'STARTING_FEE';

    /**
     * Derive the "billing month" key (YYYY-MM-01) from a timestamp.
     *
     * A billing period runs from the 12th of the previous month through
     * the 11th of the current month. So a payment on May 15 belongs to May,
     * and a payment on May 5 (still in the April-12 – May-11 window) belongs
     * to April.
     *
     * Rule: if day <= 11, step back one month.
     */
    function timestampToMonthKey(ts: string): string {
        const d = new Date(ts);
        if (d.getDate() <= 11) {
            d.setMonth(d.getMonth() - 1);
        }
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        return `${y}-${m}-01`;
    }

    const METHOD_LABELS: Record<string, string> = {
        CASH:     'Gotówka',
        CARD:     'Karta',
        TRANSFER: 'Przelew',
        BLIK:     'BLIK',
        DEBIT:    'Przelew'
    };

    function methodLabel(m: string): string {
        return METHOD_LABELS[m] ?? m;
    }

    // ── Props ────────────────────────────────────────────────────────────────

    let { data }: { data: PageData } = $props();

    const payments: Payment[] = $derived(
        data.error || !data.payments ? [] : (data.payments as Payment[])
    );

    // ── Breakdown structures ──────────────────────────────────────────────────

    interface MethodBucket { count: number; total: number; }

    interface LocationRow {
        location: string;
        methods: Map<string, MethodBucket>;
        rowTotal: number;
    }

    // A sub-section within a month: either monthly fees or entry fees
    interface Section {
        locations: Map<string, LocationRow>;
        sectionTotal: number;
        allMethods: Set<string>;
    }

    interface MonthBlock {
        monthKey: string;
        label: string;
        monthly: Section;   // MONTHLY_FEE payments
        entry: Section;     // STARTING_FEE payments
        blockTotal: number;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    function locationOf(p: Payment): string {
        return p.location?.name ?? 'Brak lokalizacji';
    }

    function formatMonth(iso: string): string {
        const [year, month] = iso.split('-');
        const d = new Date(Number(year), Number(month) - 1, 1);
        return d.toLocaleString('pl-PL', { month: 'long', year: 'numeric' });
    }

    function fmt(n: number): string {
        return n.toLocaleString('pl-PL', { style: 'currency', currency: 'PLN' });
    }

    function emptySection(): Section {
        return { locations: new Map(), sectionTotal: 0, allMethods: new Set() };
    }

    function addToSection(section: Section, p: Payment): void {
        const loc = locationOf(p);
        if (!section.locations.has(loc)) {
            section.locations.set(loc, { location: loc, methods: new Map(), rowTotal: 0 });
        }
        const row = section.locations.get(loc)!;
        if (!row.methods.has(p.paymentMethod)) {
            row.methods.set(p.paymentMethod, { count: 0, total: 0 });
        }
        const bucket       = row.methods.get(p.paymentMethod)!;
        bucket.count      += 1;
        bucket.total      += p.amount;
        row.rowTotal      += p.amount;
        section.sectionTotal += p.amount;
        section.allMethods.add(p.paymentMethod);
    }

    function paymentYear(p: Payment): number {
        if (p.paymentType === ENTRY_FEE_TYPE || !p.month) {
            return new Date(p.timeStamp).getFullYear();
        }
        return Number(p.month.split('-')[0]);
    }

    // ── Year filter ───────────────────────────────────────────────────────────

    const availableYears = $derived.by((): number[] => {
        const years = new Set<number>();
        for (const p of payments) years.add(paymentYear(p));
        return [...years].sort((a, b) => b - a);
    });

    let selectedYear = $state<number | null>(null);

    $effect(() => {
        if (availableYears.length > 0 && selectedYear === null) {
            selectedYear = availableYears[0];
        }
    });

    const filteredPayments = $derived(
        selectedYear === null
            ? payments
            : payments.filter(p => paymentYear(p) === selectedYear)
    );

    // ── Build breakdown ───────────────────────────────────────────────────────

    const breakdown = $derived.by((): MonthBlock[] => {
        const map = new Map<string, MonthBlock>();

        for (const p of filteredPayments) {
            const isEntry  = p.paymentType === ENTRY_FEE_TYPE;
            const monthKey = isEntry
                ? timestampToMonthKey(p.timeStamp)
                : (p.month ?? timestampToMonthKey(p.timeStamp));

            if (!map.has(monthKey)) {
                map.set(monthKey, {
                    monthKey,
                    label: formatMonth(monthKey),
                    monthly: emptySection(),
                    entry:   emptySection(),
                    blockTotal: 0,
                });
            }

            const block = map.get(monthKey)!;
            addToSection(isEntry ? block.entry : block.monthly, p);
            block.blockTotal += p.amount;
        }

        return [...map.entries()]
            .sort(([a], [b]) => b.localeCompare(a))
            .map(([, v]) => v);
    });

    const grandTotal = $derived(breakdown.reduce((s, b) => s + b.blockTotal, 0));
    const grandCount = $derived(filteredPayments.length);

    // ── Collapse state ────────────────────────────────────────────────────────

    let collapsed = $state<Set<string>>(new Set());

    function toggle(key: string) {
        const next = new Set(collapsed);
        next.has(key) ? next.delete(key) : next.add(key);
        collapsed = next;
    }
</script>

<div class="page">

    <h1>Zestawienie płatności</h1>

    {#if !data.error && payments.length > 0}
        <div id="filterHolder">
            <label for="yearSelect">Rok:</label>
            <select
                    id="yearSelect"
                    bind:value={selectedYear}
                    onchange={() => { collapsed = new Set(); }}
            >
                {#each availableYears as year}
                    <option value={year}>{year}</option>
                {/each}
            </select>
        </div>
    {/if}

    {#if data.error}
        <p class="no-results">Nie udało się załadować danych.</p>

    {:else if payments.length === 0}
        <p class="no-results">Brak zarejestrowanych płatności.</p>

    {:else}
        <div class="summary-bar">
            <div class="summary-item">
                <span class="summary-value">{fmt(grandTotal)}</span>
                <span class="summary-label">Łącznie zebrano</span>
            </div>
            <div class="summary-item">
                <span class="summary-value">{grandCount}</span>
                <span class="summary-label">Transakcji</span>
            </div>
            <div class="summary-item">
                <span class="summary-value">{breakdown.length}</span>
                <span class="summary-label">Okresów</span>
            </div>
        </div>

        {#each breakdown as block}
            {@const isCollapsed = collapsed.has(block.monthKey)}

            <div class="block">
                <button
                        class="block-header"
                        onclick={() => toggle(block.monthKey)}
                        aria-expanded={!isCollapsed}
                >
                    <span class="block-label">{block.label}</span>
                    <span class="block-right">
                        <span class="block-total">{fmt(block.blockTotal)}</span>
                        <svg viewBox="0 0 16 16" fill="none" class="chevron" class:rotated={!isCollapsed}>
                            <path d="M4 6l4 4 4-4" stroke="currentColor" stroke-width="1.5"
                                  stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </span>
                </button>

                {#if !isCollapsed}
                    <!-- Monthly fees section -->
                    {#if block.monthly.locations.size > 0}
                        {@const section = block.monthly}
                        {@const methodList = [...section.allMethods]}
                        <div class="section-header">
                            <span class="section-label">Składki miesięczne</span>
                            <span class="section-total">{fmt(section.sectionTotal)}</span>
                        </div>
                        <div class="table-wrap">
                            <table>
                                <thead>
                                <tr>
                                    <td>Lokalizacja</td>
                                    {#each methodList as m}<td>{methodLabel(m)}</td>{/each}
                                    <td>Suma</td>
                                </tr>
                                </thead>
                                <tbody>
                                {#each [...section.locations.values()] as row}
                                    <tr>
                                        <td class="loc-cell">{row.location}</td>
                                        {#each methodList as m}
                                            {@const bucket = row.methods.get(m)}
                                            <td>
                                                {#if bucket}
                                                    <div class="cell-amount">{fmt(bucket.total)}</div>
                                                    <div class="cell-count">{bucket.count} wpł.</div>
                                                {:else}
                                                    <span class="cell-empty">—</span>
                                                {/if}
                                            </td>
                                        {/each}
                                        <td class="total-cell">{fmt(row.rowTotal)}</td>
                                    </tr>
                                {/each}
                                </tbody>
                                <tfoot>
                                <tr>
                                    <td class="loc-cell">Suma</td>
                                    {#each methodList as m}
                                        {@const colTotal = [...section.locations.values()]
                                            .reduce((s, r) => s + (r.methods.get(m)?.total ?? 0), 0)}
                                        {@const colCount = [...section.locations.values()]
                                            .reduce((s, r) => s + (r.methods.get(m)?.count ?? 0), 0)}
                                        <td>
                                            <div class="cell-amount">{fmt(colTotal)}</div>
                                            <div class="cell-count">{colCount} wpł.</div>
                                        </td>
                                    {/each}
                                    <td class="total-cell">{fmt(section.sectionTotal)}</td>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    {/if}

                    <!-- Entry fees section -->
                    {#if block.entry.locations.size > 0}
                        {@const section = block.entry}
                        {@const methodList = [...section.allMethods]}
                        <div class="section-header entry-section-header">
                            <span class="section-label">Wpisowe</span>
                            <span class="section-total">{fmt(section.sectionTotal)}</span>
                        </div>
                        <div class="table-wrap">
                            <table>
                                <thead>
                                <tr>
                                    <td>Lokalizacja</td>
                                    {#each methodList as m}<td>{methodLabel(m)}</td>{/each}
                                    <td>Suma</td>
                                </tr>
                                </thead>
                                <tbody>
                                {#each [...section.locations.values()] as row}
                                    <tr>
                                        <td class="loc-cell">{row.location}</td>
                                        {#each methodList as m}
                                            {@const bucket = row.methods.get(m)}
                                            <td>
                                                {#if bucket}
                                                    <div class="cell-amount">{fmt(bucket.total)}</div>
                                                    <div class="cell-count">{bucket.count} wpł.</div>
                                                {:else}
                                                    <span class="cell-empty">—</span>
                                                {/if}
                                            </td>
                                        {/each}
                                        <td class="total-cell">{fmt(row.rowTotal)}</td>
                                    </tr>
                                {/each}
                                </tbody>
                                <tfoot>
                                <tr>
                                    <td class="loc-cell">Suma</td>
                                    {#each methodList as m}
                                        {@const colTotal = [...section.locations.values()]
                                            .reduce((s, r) => s + (r.methods.get(m)?.total ?? 0), 0)}
                                        {@const colCount = [...section.locations.values()]
                                            .reduce((s, r) => s + (r.methods.get(m)?.count ?? 0), 0)}
                                        <td>
                                            <div class="cell-amount">{fmt(colTotal)}</div>
                                            <div class="cell-count">{colCount} wpł.</div>
                                        </td>
                                    {/each}
                                    <td class="total-cell">{fmt(section.sectionTotal)}</td>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    {/if}
                {/if}
            </div>
        {/each}
    {/if}
</div>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Ubuntu:wght@300;400;500;700&display=swap');

    * {
        font-family: 'Ubuntu', sans-serif;
        font-weight: normal;
        box-sizing: border-box;
        margin: 0;
        padding: 0;
    }

    .page {
        display: flex;
        flex-direction: column;
        gap: 15px;
        padding: 20px;
    }

    h1 {
        font-size: 1.6em;
        font-weight: 500;
        color: var(--color-text-primary);
    }

    #filterHolder {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        padding: 10px 16px;
        display: flex;
        flex-direction: row;
        align-items: center;
        gap: 10px;
        width: fit-content;
    }

    #filterHolder label {
        color: var(--color-text-secondary);
        font-size: 0.95em;
        user-select: none;
    }

    select {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 15px;
        color: var(--color-text-primary);
        padding: 6px 14px;
        cursor: pointer;
        font-family: 'Ubuntu', sans-serif;
        font-size: 0.95em;
    }

    select:focus { outline: none; }

    .no-results {
        font-size: 2em;
        text-align: center;
        color: var(--color-text-secondary);
    }

    /* ── Summary bar ── */
    .summary-bar {
        display: flex;
        flex-direction: row;
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        overflow: hidden;
    }

    .summary-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 14px 10px;
        gap: 3px;
        background-color: var(--color-background-secondary);
    }

    .summary-item + .summary-item {
        border-left: 2px solid var(--color-border);
    }

    .summary-value {
        font-size: 1.3em;
        font-weight: 500;
        color: var(--color-text-primary);
    }

    .summary-label {
        font-size: 0.75em;
        color: var(--color-text-secondary);
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }

    /* ── Month block ── */
    .block {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        overflow: hidden;
    }

    .block-header {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
        width: 100%;
        padding: 12px 16px;
        background-color: var(--color-background-secondary);
        border: none;
        cursor: pointer;
        color: var(--color-text-primary);
        text-align: left;
        gap: 10px;
    }

    .block-header:hover {
        background-color: var(--color-background-primary);
    }

    .block-label {
        font-size: 1.05em;
        font-weight: 500;
        color: var(--color-text-primary);
    }

    .block-right {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-shrink: 0;
    }

    .block-total {
        font-weight: 500;
        color: var(--color-text-primary);
    }

    .chevron {
        width: 18px;
        height: 18px;
        color: var(--color-text-secondary);
        transition: transform 0.2s;
        flex-shrink: 0;
    }
    .chevron.rotated { transform: rotate(180deg); }

    /* ── Section headers (Składki / Wpisowe) ── */
    .section-header {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
        padding: 8px 16px;
        border-top: 2px solid var(--color-border);
        background-color: var(--color-background-primary);
    }

    .section-label {
        font-size: 0.8em;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.07em;
        color: var(--color-text-secondary);
    }

    .section-total {
        font-size: 0.9em;
        font-weight: 500;
        color: var(--color-text-primary);
    }

    /* ── Table ── */
    .table-wrap {
        overflow-x: auto;
    }

    table {
        width: 100%;
        table-layout: fixed;
        border-collapse: collapse;
    }

    thead {
        background-color: var(--color-background-secondary);
        height: 45px;
    }

    thead td {
        font-weight: bold;
        padding: 10px;
        text-align: center;
        border-bottom: 2px solid var(--color-border);
    }

    thead td:first-child {
        text-align: left;
        padding-left: 15px;
    }

    tbody tr:nth-child(even) {
        background-color: var(--color-background-secondary);
    }

    tbody tr:hover {
        background-color: var(--color-background-secondary);
        opacity: 0.85;
    }

    td {
        text-align: center;
        padding: 8px 5px;
        color: var(--color-text-primary);
    }

    td:first-child {
        text-align: left;
        padding-left: 15px;
    }

    .loc-cell {
        font-weight: 500;
        color: var(--color-text-primary);
    }

    .cell-amount {
        font-weight: 500;
        color: var(--color-text-primary);
    }

    .cell-count {
        font-size: 0.78em;
        color: var(--color-text-secondary);
        margin-top: 2px;
    }

    .cell-empty {
        color: var(--color-text-secondary);
        opacity: 0.4;
    }

    .total-cell {
        font-weight: 700;
        color: var(--color-text-primary);
    }

    tfoot tr {
        background-color: var(--color-background-secondary);
        border-top: 2px solid var(--color-border);
    }

    tfoot .loc-cell {
        font-weight: 700;
        font-size: 0.85em;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        color: var(--color-text-secondary);
    }

    @media (max-width: 560px) {
        .summary-bar { flex-direction: column; }
        .summary-item + .summary-item {
            border-left: none;
            border-top: 2px solid var(--color-border);
        }
    }
</style>
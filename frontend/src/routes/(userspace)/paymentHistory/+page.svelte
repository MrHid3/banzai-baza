<!-- src/routes/platnosci/+page.svelte -->
<script lang="ts">
    import { enhance } from '$app/forms';

    type Location = { id: number; name: string; shortname: string; isActive: boolean };
    type PayerIn = { email: string };
    type Payer = { name: string; surname: string };
    type Payment = {
        uuid: string;
        amount: string;
        paymentMethod: 'CASH' | 'DEBIT';
        paymentType: 'MONTHLY_FEE' | 'STARTING_FEE';
        month: string | null; // "YYYY-MM-DD" (first of month from LocalDate)
        timeStamp: string;    // ISO datetime
        payerIn: PayerIn;
        payer: Payer;
        location: Location;
        comment: string | null;
    };

    let { data } = $props<{ data: { payments: Payment[] } }>();

    const POLISH_MONTHS = ['Styczeń','Luty','Marzec','Kwiecień','Maj','Czerwiec',
        'Lipiec','Sierpień','Wrzesień','Październik','Listopad','Grudzień'];

    function timestampToPeriod(ts: string): { month: number; year: number } {
        const d = new Date(ts);
        const day = d.getDate();
        let month = d.getMonth();
        let year = d.getFullYear();
        if (day < 11) {
            if (month === 0) { month = 11; year--; } else { month--; }
        }
        return { month, year };
    }

    function getPaymentPeriod(p: Payment): { month: number; year: number } {
        if (p.month) {
            const d = new Date(p.month);
            return { month: d.getMonth(), year: d.getFullYear() };
        }
        return timestampToPeriod(p.timeStamp);
    }

    function periodKey(p: { month: number; year: number }): string {
        return `${p.year}-${String(p.month + 1).padStart(2, '0')}`;
    }

    function periodLabel(p: { month: number; year: number }): string {
        return `${POLISH_MONTHS[p.month]} ${p.year}`;
    }

    function formatTs(ts: string): string {
        const d = new Date(ts);
        const dd = String(d.getDate()).padStart(2, '0');
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        const hh = String(d.getHours()).padStart(2, '0');
        const min = String(d.getMinutes()).padStart(2, '0');
        return `${dd}.${mm}.${d.getFullYear()} ${hh}:${min}`;
    }

    function lastThreeMonthKeys(): { key: string; label: string }[] {
        const now = new Date();
        return Array.from({ length: 3 }, (_, i) => {
            let m = now.getMonth() - i;
            let y = now.getFullYear();
            if (m < 0) { m += 12; y--; }
            return { key: `${y}-${String(m + 1).padStart(2, '0')}`, label: periodLabel({ month: m, year: y }) };
        });
    }

    let filterText = $state('');
    let filterLocation = $state('');
    let filterType = $state('');
    let filterPeriod = $state('');

    let payments = $state(data.payments);

    const availableLocations = $derived(
        [...new Set(payments.map(p => p.location.name))].sort()
    );

    const periodOptions = lastThreeMonthKeys();

    const filtered = $derived.by(() => {
        const txt = filterText.toLowerCase().trim();
        return payments.filter(p => {
            if (txt) {
                const hay = `${p.payer.name} ${p.payer.surname} ${p.payerIn.email} ${p.location.name} ${p.location.shortname} ${p.comment ?? ''}`.toLowerCase();
                if (!hay.includes(txt)) return false;
            }
            if (filterLocation && p.location.name !== filterLocation) return false;
            if (filterType && p.paymentType !== filterType) return false;
            if (filterPeriod) {
                if (filterPeriod === 'STARTING_FEE') {
                    if (p.paymentType !== 'STARTING_FEE') return false;
                } else {
                    if (periodKey(getPaymentPeriod(p)) !== filterPeriod) return false;
                }
            }
            return true;
        });
    });

</script>

<svelte:head>
    <title>Baza - Szczegoły Płatności</title>
</svelte:head>

<div class="min-h-screen" style="background: var(--background-primary); color: var(--text-primary)">
    <div class="max-w-7xl mx-auto">

        <!-- Filters -->
        <div
                class="grid gap-3 mb-5 p-4! rounded-xl border"
                style="
        grid-template-columns: 2fr 1fr 1fr 1fr;
        background: var(--background-secondary);
        border-color: var(--border);
      "
        >
            <div>
                <div class="text-xs mb-1" style="color: var(--text-secondary)">Szukaj</div>
                <input
                        type="text"
                        bind:value={filterText}
                        placeholder="Imię, nazwisko, email…"
                        class="w-full px-3! py-2! text-sm rounded-lg border outline-none"
                        style="
            background: var(--background-primary);
            border-color: var(--border);
            color: var(--text-primary);
          "
                />
            </div>
            <div>
                <div class="text-xs mb-1" style="color: var(--text-secondary)">Lokalizacja</div>
                <select
                        bind:value={filterLocation}
                        class="w-full px-3! py-2! text-sm rounded-lg border outline-none"
                        style="
            background: var(--background-primary);
            border-color: var(--border);
            color: var(--text-primary);
          "
                >
                    <option value="">Wszystkie</option>
                    {#each availableLocations as loc}
                        <option value={loc}>{loc}</option>
                    {/each}
                </select>
            </div>
            <div>
                <div class="text-xs mb-1" style="color: var(--text-secondary)">Metoda</div>
                <select
                        bind:value={filterType}
                        class="w-full px-3! py-2! text-sm rounded-lg border outline-none"
                        style="
            background: var(--background-primary);
            border-color: var(--border);
            color: var(--text-primary);
          "
                >
                    <option value="">Wszystkie</option>
                    <option value="MONTHLY_FEE">Składka miesięczna</option>
                    <option value="STARTING_FEE">Wpisowe</option>
                </select>
            </div>
            <div>
                <div class="text-xs mb-1" style="color: var(--text-secondary)">Okres</div>
                <select
                        bind:value={filterPeriod}
                        class="w-full px-3! py-2! text-sm rounded-lg border outline-none"
                        style="
            background: var(--background-primary);
            border-color: var(--border);
            color: var(--text-primary);
          "
                >
                    <option value="">Wszystkie</option>
                    {#each periodOptions as opt}
                        <option value={opt.key}>{opt.label}</option>
                    {/each}
                    <option value="STARTING_FEE">Wpisowe</option>
                </select>
            </div>
        </div>

        <p class="text-sm mb-3" style="color: var(--text-secondary)">
            Znaleziono: {filtered.length} płatności
        </p>

        <!-- Table -->
        <div class="rounded-xl overflow-hidden border" style="border-color: var(--border)">
            <table class="w-full text-sm border-collapse" style="table-layout: fixed">
                <colgroup>
                    <col style="width: 14%">
                    <col style="width: 10%">
                    <col style="width: 16%">
                    <col style="width: 13%">
                    <col style="width: 11%">
                    <col style="width: 10%">
                    <col style="width: 9%">
                    <col style="width: 12%">
                    <col style="width: 5%">
                </colgroup>
                <thead>
                <tr style="background: var(--background-special); color: var(--background-primary)">
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Członek</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Lokalizacja</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Wprowadzający</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Data</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Okres</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Kwota</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Typ</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Metoda</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Komentarz</th>
                    <th class="px-3! py-2.5! font-medium text-left text-xs">Akcja</th>
                </tr>
                </thead>
                <tbody>
                {#if filtered.length === 0}
                    <tr>
                        <td
                                colspan="9"
                                class="text-center py-8! text-sm"
                                style="
                  color: var(--text-secondary);
                  background: var(--background-secondary);
                "
                        >
                            Brak wyników dla podanych filtrów.
                        </td>
                    </tr>
                {/if}
                {#each filtered as payment, i (payment.uuid)}
                    {@const period = getPaymentPeriod(payment)}
                    {@const periodStr =
                        payment.paymentType === 'STARTING_FEE' && !payment.month
                            ? `Wpisowe (${periodLabel(period)})`
                            : periodLabel(period)}
                    <tr
                            style="
                background: {i % 2 === 0
                  ? 'var(--color-background-secondary)'
                  : 'var(--color-background-primary)'};
                border-bottom: 0.5px solid var(--color-border);
              "
                    >
                        <td class="px-3! py-2! truncate" title="{payment.payer.name} {payment.payer.surname}">
                            {payment.payer.name} {payment.payer.surname}
                        </td>
                        <td class="px-3! py-2! truncate" title={payment.location.name}>
                            {payment.location.shortname}
                        </td>
                        <td class="px-3! py-2! truncate" title={payment.payerIn.email}>
                            {payment.payerIn.email}
                        </td>
                        <td class="px-3! py-2! whitespace-nowrap">
                            {formatTs(payment.timeStamp)}
                        </td>
                        <td class="px-3! py-2! truncate" title={periodStr}>
                            {periodStr}
                        </td>
                        <td class="px-3! py-2! truncate" title={payment.amount}>
                            {payment.amount}
                        </td>
                        <td class="px-3! py-2!">
                <span
                        class="inline-block px-2! py-0.5! rounded text-xs font-medium"
                        style="
                    background: {payment.paymentType === 'MONTHLY_FEE' ? '#b0c4b0' : '#c4b0b0'};
                    color: {payment.paymentType === 'MONTHLY_FEE' ? '#1a2e1a' : '#2e1a1a'};
                  "
                >
                  {payment.paymentType === 'MONTHLY_FEE' ? 'Składka' : 'Wpisowe'}
                </span>
                        </td>
                        <td class="px-3! py-2!">
                <span
                        class="inline-block px-2 py-0.5 rounded text-xs font-medium"
                        style="
                    background: {payment.paymentMethod === 'CASH' ? '#c0c0c0' : 'var(--background-special)'};
                    color: {payment.paymentMethod === 'CASH' ? '#2c2c2c' : '#ededed'};
                  "
                >
                  {payment.paymentMethod === 'CASH' ? 'Gotówka' : 'Karta'}
                </span>
                        </td>
                        <td class="px-3! py-2! truncate" title={payment.comment ?? ''}>
                            {payment.comment || '—'}
                        </td>
                        <td class="px-3! py-2!">
                            <form
                                    method="POST"
                                    action="?/deletePayment"
                                    use:enhance={({ formData }) => {
                    return async ({ result, update }) => {
                      if (result.type === 'success') {
                        payments = payments.filter(p => p.uuid !== payment.uuid);
                      } else {
                        await update();
                      }
                    };
                  }}
                            >
                                <input type="hidden" name="uuid" value={payment.uuid} />
                                <button
                                        type="submit"
                                        class="px-2 py-1 text-xs rounded border cursor-pointer transition-colors"
                                        style="
                      background: transparent;
                      border-color: var(--border);
                      color: var(--text-secondary);
                    "
                                        onmouseenter={(e) => {
                      (e.currentTarget as HTMLButtonElement).style.background = 'var(--background-special)';
                      (e.currentTarget as HTMLButtonElement).style.color = '#ededed';
                      (e.currentTarget as HTMLButtonElement).style.borderColor = 'var(--background-special)';
                    }}
                                        onmouseleave={(e) => {
                      (e.currentTarget as HTMLButtonElement).style.background = 'transparent';
                      (e.currentTarget as HTMLButtonElement).style.color = 'var(--color-text-secondary)';
                      (e.currentTarget as HTMLButtonElement).style.borderColor = 'var(--color-border)';
                    }}
                                >
                                    Usuń
                                </button>
                            </form>
                        </td>
                    </tr>
                {/each}
                </tbody>
            </table>
        </div>
    </div>
</div>

<style>
    @import "tailwindcss";
</style>
<script lang="ts">
    import LocationSelect from '$lib/LocationSelect.svelte';
    import {enhance} from '$app/forms';
    import {locations} from '$lib/stores/locations.svelte';
    import {onMount, untrack} from 'svelte';
    import {resolve} from '$app/paths';
    import {MediaQuery} from 'svelte/reactivity';

    const {data, form} = $props();

    let members = $state(data.payments ?? []);
    let filteredMembers = $state(members);
    const isMobile = new MediaQuery('max-width: 1000px');

    onMount(() => {
        locations.load(true);
    });

    $effect(() => {
        members = data.payments.sort((a, b) => a.member.uuid.localeCompare(b.member.uuid)) ?? [];
    });

    $effect(() => {
        let result = members;

        if (selectedLocation != null) {
            result = result.filter((m) => {
                return m.member.location.id == selectedLocation?.id;
            });
        }


        const search = memberTextFilter;
        if (search.length > 1 || selectedCategory != -1) {
            result = result.filter((m) => {
                return (
                    m.member.name?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.surname?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.email?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.phoneNumber?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.comment?.toLowerCase().includes(search.toLowerCase())
                ) && (selectedCategory == -1 || selectedCategory == null || m.member.categories.some(a => a.id == selectedCategory));
            });
        }

        untrack(() => {
            filteredMembers = result;
        });
    });

    let memberTextFilter = $state('');
    let selectedLocation = $state(null);
    let selectedCategory = $state(-1);
    let showEntryFee = $state(false);

    const monthNames = ['Grudzień', 'Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień', 'Styczeń', 'Luty'];

    const currentMonth = new Date().getMonth() + 1;
    // const currentMonth = 1
    const currentYear = new Date().getFullYear();

    const monthString = (month: number) => {
        return month.toString().length == 1 ? '0' + month.toString() : month.toString();
    };

    function resolveMonthKey(i: number): string {
        const month = currentMonth - i;
        if (month > 0) {
            return `${currentYear}-${monthString(month)}-01`;
        } else {
            return `${currentYear - 1}-${monthString(month + 12)}-01`;
        }
    }

    const multiplierMap = new Map(
        ($locations.data ?? []).map(loc => [
            loc.id,
            new Map(
                (data.multipliers ?? [])
                    .filter(m => m.location.id === loc.id)
                    .map(m => [parseInt(m.month.split('-')[1]), m])
            )
        ])
    );

    let monthTotals = $derived.by(() => {
        const keys = [1, 0, -1].map(resolveMonthKey);
        const totals = keys.map(() => ({DEBIT: 0, CASH: 0}));
        for (const m of filteredMembers) {
            for (const p of m.member.payments) {
                const i = keys.indexOf(p.month);
                if (i !== -1) totals[i][p.paymentMethod] += p.amount;
            }
        }
        return totals;
    });

    let startingTotals = $derived.by(() => {
        let totals = {DEBIT: 0, CASH: 0};
        for (const m of filteredMembers) {
            for (const p of m.member.payments) {
                if(p.paymentType == "STARTING_FEE"){
                    totals[p.paymentMethod] += p.amount
                }
            }
        }
        return totals;
    })

    let commentDrafts = $state({});
    let openComment = $state("");

    function keyFor(payerUuid, month, year) {
        return `${payerUuid}-${month}-${year}`;
    }
</script>
<svelte:head>
    <title>Baza - Płatności</title>
</svelte:head>


{#snippet payment(payment, type, month, year, payerUuid, payerStart)}
    {#if type != "STARTING_FEE" && new Date(payerStart).getMonth() - new Date(`${year}-${monthString(month)}`).getMonth() > 0}
        <abbr class="td payment ok border-none! w-full! xl:max-w-1/4! xl:w-1/4"
              title="Nie chodził"
              style="font-style: unset; text-decoration: unset">
                <span>0</span>
        </abbr>
    {:else if payment}
        <abbr class="td payment ok border-none! w-full! xl:max-w-1/4! xl:w-1/4"
              title={payment.comment}
              style="font-style: unset; text-decoration: unset">
            <form action="?/deletePayment"
                  class="flex-1 *:bg-(--input)! bg-(--input) shadow-md shadow-slate-950/40 rounded-2xl"
                  method="post" use:enhance>
                {#if payment.paymentMethod == "CASH"}
                    <i>💵</i>
                {:else}
                    <i>💳</i>
                {/if}
                <input type="hidden" name="paymentUuid" value={payment.uuid}>
                <span>{payment.amount}</span>
                <button type="submit" aria-label="usuń"
                        class="p-2! text-2xl! font-bold! text-(--click-dark)! hover:text-(--text-primary)! duration-200">
                    X
                </button>
            </form>
        </abbr>
    {:else}
        {@const k = keyFor(payerUuid, month, year)}
        <div class="td payment bad last:rounded-r-2xl! border-none! w-full! xl:max-w-1/4! xl:w-1/4">
            <form action="?/addPayment" method="POST"
                  class="flex-1 bg-(--link)! *:bg-(--link) outline-0! *:outline-0! border-0 b*:border-0! shadow-md shadow-slate-950/40"
                  use:enhance>
                <input type="hidden" name="paymentType" value={type}>
                <input type="hidden" name="month" value={month}>
                <input type="hidden" name="year" value={year}>
                <input type="hidden" name="payerUuid" value={payerUuid}>
                <select name="paymentMethod" class="bg-transparent!">
                    <option value="CASH">💵</option>
                    <option value="DEBIT">💳</option>
                </select>
                <input type="number" name="amount" value={0} required min="0" max="1000" class="bg-transparent! w-10">
                <button type="button" class="bg-transparent! rounded-none!" onclick={() => openComment = k}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30"
                         class="cursor-pointer">
                        <path
                                d="M544 128C544 110.3 529.7 96 512 96L128 96C110.3 96 96 110.3 96 128C96 145.7 110.3 160 128 160L512 160C529.7 160 544 145.7 544 128zM544 384C544 366.3 529.7 352 512 352L128 352C110.3 352 96 366.3 96 384C96 401.7 110.3 416 128 416L512 416C529.7 416 544 401.7 544 384zM96 256C96 273.7 110.3 288 128 288L512 288C529.7 288 544 273.7 544 256C544 238.3 529.7 224 512 224L128 224C110.3 224 96 238.3 96 256zM544 512C544 494.3 529.7 480 512 480L128 480C110.3 480 96 494.3 96 512C96 529.7 110.3 544 128 544L512 544C529.7 544 544 529.7 544 512z"/>
                    </svg>
                </button>
                <input type="hidden" name="comment" value={commentDrafts[k] ?? payment?.comment ?? ''}>
                {#if openComment === k}
                    <div
                            class="fixed inset-0 bg-black/40! backdrop-blur-sm"
                    >
                        <div
                                class="bg-(--background-secondary)! absolute! top-1/2 left-1/2 rounded-md -translate-x-1/2 translate-y-[-200%] flex flex-col gap-5 p-2 md:w-1/4!:w-full! h-fit">
                                <textarea name="comment"
                                          class="bg-(--input) text-(--text-secondary) block rounded-md p-2! h-20! shadow-md shadow-slate-950/40" value={commentDrafts[k] ?? payment?.comment ?? ''}
                                          oninput={(e) => commentDrafts[k] = e.currentTarget.value}></textarea>
                            <button type="button" onclick={() => openComment = ""}
                                    class="block text-gray-200 bg-(--click) rounded-sm! text-2xl cursor-pointer p-1! hover:bg-neutral-800 duration-200 shadow-md shadow-slate-950/40">Zapisz</button>
                        </div>
                    </div>
                {/if}
                <button type="submit" aria-label="Zapisz" class="bg-transparent!">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30"
                         class="cursor-pointer">
                        <path
                                d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/>
                    </svg>
                </button>
            </form>
        </div>
    {/if}
{/snippet}

<div class="bg-(--background-primary) shadow-md shadow-slate-950/20 rounded-2xl p-4">
    <a class="absolute top-16 left-4 p-3 rounded-xl bg-neutral-200 border-2 border-neutral-400 text-neutral-400 hover:text-neutral-600 hover:text-shadow-2 hover:text-shadow-black/20 duration-200 desktop"
       href={resolve('/paymentHistory')}>Szczegóły</a>
    <div class="bg-(--background-secondary) text-(--text-primary-dark) shadow-md shadow-slate-950/20 flex flex-col xl:flex-row gap-2" id="filterHolder">
        <span class="desktop">Znajdź:</span>
        <input bind:value={memberTextFilter} class="input bg-(--input) text-(--text-primary)!" type="text" placeholder={isMobile.current? "Znajdź..." : ""}/>
        <span class="desktop">Filtruj po lokalizacji:</span>
        <LocationSelect all={true} bind:location={selectedLocation} short={false} mobile={isMobile.current}></LocationSelect>
        <span class="desktop">Filtruj po kategorii:</span>
        <select bind:value={selectedCategory} class="input text-(--text-primary) p-2!">
            <option value={-1}>Wszystkie{isMobile.current ? " kategorie" : ""}</option>
            {#each data.categories as category (category.id)}
                <option value={category.id}>{category.shortname}</option>
            {/each}
        </select>
        <label class="desktop" for="showEntryFeeCheckbox">Pokaż wpisowe</label>
        <input bind:checked={showEntryFee} class="desktop" id="showEntryFeeCheckbox" type="checkbox">

        {#if form?.error}
            <span class="error">{form.error}</span>
        {/if}
    </div>


    {#if !isMobile.current}
        <div class="tabletop desktop">
            <div class="thead">
                <div class="tr *:bg-(--background-secondary)/60 backdrop-blur-lg outline-1 outline-white/60 *:p-2! *:align-middle text-(--text-primary-dark) shadow-md shadow-slate-950/20 rounded-2xl">
                    <div class="td rounded-l-2xl!">#</div>
                    <div class="td">Imię</div>
                    <div class="td">Nazwisko</div>
                    <div class="td">Lokalizacja</div>
                    <div class="td">Cena/mieś.</div>
                    {#if showEntryFee}
                        <div class="td flex-col flex">
                            <div>Wpisowe</div>
                            <div class="flex flex-row gap-2 justify-center">
                                (<span
                                    class="text-yellow-600">{startingTotals.DEBIT}</span>
                                <span
                                        class="text-green-600">{startingTotals.CASH}</span>)
                            </div>
                        </div>
                    {/if}
                    <div class="td flex-col flex">
                        <div>
                            {monthNames[currentMonth - 1]}
                        </div>
                        <div class="flex flex-row gap-2 justify-center">
                            (<span
                                class="text-yellow-600">{monthTotals[0].DEBIT}</span>
                            <span
                                    class="text-green-600">{monthTotals[0].CASH}</span>)
                        </div>
                    </div>
                    <div class="td">
                        <div>
                            {monthNames[currentMonth]}
                        </div>
                        <div class="flex flex-row gap-2 justify-center">
                            (<span
                                class="text-yellow-600">{monthTotals[1].DEBIT}</span>
                            <span
                                    class="text-green-600">{monthTotals[1].CASH}</span>)
                        </div>
                    </div>
                    <div class="td rounded-r-2xl!">
                        <div>
                            {monthNames[currentMonth + 1]}
                        </div>
                        <div class="flex flex-row gap-2 justify-center">
                            (<span
                                class="text-yellow-600">{monthTotals[2].DEBIT}</span>
                            <span
                                    class="text-green-600">{monthTotals[2].CASH}</span>)
                        </div>
                    </div>
                </div>
            </div>
            <div class="tbody">
                {#each filteredMembers as member, i (member.member.uuid)}
                    <div
                            class="tr border-none rounded-2xl! duration-150 bg-(--background-secondary)!
						text-(--text-primary-dark) shadow-md shadow-slate-950/20">
                        <div
                                class="td rounded-l-2xl!">{ i + 1}</div>
                        <div class="td">{member.member.name != "" ? member.member.name : "- -"}</div>
                        <div class="td">{member.member.surname != "" ? member.member.surname : "- -"}</div>
                        <div class="td">{member.member.location.shortname}</div>
                        <div
                                class="td">{member.member.monthlyFee * Number(multiplierMap.get(member.member.location.id)?.get(currentMonth)?.multiplier ?? 1)}</div>
                        {#if showEntryFee}
                            {@render payment(
                                member.payments.find((a) => a.month == null),
                                "STARTING_FEE",
                                null,
                                null,
                                member.member.uuid,
                                member.member.createdAt
                            )}
                        {/if}
                        {#each [1, 0, -1] as i}
                            {@render payment(
                                member.payments.find((a) => a.month == `${currentYear}-${monthString(currentMonth - i)}-01`),
                                "MONTHLY_FEE",
                                currentMonth - i > 0 ? currentMonth - i : currentMonth - i + 12,
                                currentMonth - i > 0 ? currentYear : currentYear - 1,
                                member.member.uuid,
                                member.member.createdAt
                            )}
                        {/each}
                    </div>
                {/each}
            </div>
        </div>
    {/if}
    {#if isMobile.current}
        {#each filteredMembers as member (member.member.uuid)}
            <div class="mobile bg-(--background-secondary) shadow-md shadow-slate-950/40 text-(--text-secondary)"
                 style="padding: 20px; border-radius: 15px; margin: 15px 0">
                <div class="horizontal"><span class="bold flex-1">Imię</span><span
                        class="text-right flex-1 block">{member.member.name}</span></div>
                <div class="horizontal"><span class="bold flex-1">Nazwisko</span><span
                        class="text-right flex-1 block">{member.member.surname}</span></div>
                {#if !selectedLocation}
                    <div class="horizontal"><span
                            class="bold flex-1">Lokalizacja</span><span
                            class="text-right flex-1 block">{member.member.location.shortname}</span></div>
                {/if}
                <div class="horizontal"><span class="bold flex-1">Cena/mieś.</span><span
                        class="text-right flex-1 block">{member.member.monthlyFee}</span></div>
                <div class="horizontal"><span class="bold flex-1 flex items-center">Wpis</span><span
                        class="flex-3">{@render payment(
                    member.payments.find((a) => a.month == null),
                    "STARTING_FEE",
                    null,
                    null,
                    member.member.uuid,
                    member.member.createdAt
                )}</span></div>
                {#each [1, 0, -1] as i}
                    <div class="horizontal flex"><span
                            class="bold flex-1 flex items-center">{(monthNames[currentMonth - i]).substring(0, 3)}</span><span
                            class="flex-3">
                {@render payment(
                    member.payments.find((a) => a.month == `${currentYear}-${monthString(currentMonth - i)}-01`),
                    "MONTHLY_FEE",
                    currentMonth - i > 0 ? currentMonth - i : currentMonth - i + 12,
                    currentMonth - i > 0 ? currentYear : currentYear - 1,
                    member.member.uuid,
                    member.member.createdAt
                )}
                </span></div>
                {/each}
            </div>
        {/each}
    {/if}
</div>
<style>

    @import url('https://fonts.googleapis.com/css2?family=Noto+Color+Emoji&family=Noto+Emoji:wght@300..700&display=swap');

    @reference "tailwindcss";

    * {
        font-family: 'Ubuntu', sans-serif, "Noto Color Emoji", sans-serif;
        font-optical-sizing: auto;
    }

    #filterHolder {
        border-radius: 15px;
        width: 100%;
        padding: 10px;
        margin: 0 0 10px 0;
    }

    .input {
        @apply
        bg-(--input)!
        text-center
        max-w-full
        p-1!
        rounded-lg!
        shadow-md shadow-slate-950/40
        outline-(--active)
        ;
    }

    svg {
        fill: var(--click-dark) !important;
        transition-duration: 200ms;
    }

    button:hover svg,
    svg:hover {
        fill: var(--text-primary) !important;
    }

    option,
    select {
        text-align: center !important;
    }

    input:focus {
        border: none;
        outline: none;
    }

    .tbody {
        display: table-row-group;
        width: 100%;
    }

    .tabletop {
        display: table;
        border-spacing: 0 8px;
        width: 100%;
    }

    .thead {
        border-radius: 15px;
        /*height: 45px;*/
        display: table-header-group;
        position: sticky;
        top: 10px;
    }

    .thead .td {
        max-width: none;
        text-transform: math-auto;
        display: table-cell;
        padding: 10px 0;
    }

    .tr {
        display: table-row !important;
        align-items: center;
        margin-bottom: 10px;
    }

    .td {
        text-align: center;
        padding: 5px;
        display: table-cell !important;
    }

    .td:not(.payment) {
        overflow-wrap: break-word;
        line-height: 1.25;
        max-width: 12rem;
    }

    .td.payment {
        display: table-cell;
    }

    .td.payment.ok span {
        text-align: center;
        @apply
        text-lime-400
        ;
    }

    .td.payment.bad form {
        border-radius: 15px;
    }

    .td.payment.bad input {
        @apply
        text-rose-500
        ;
    }

    .td.payment form {
        display: flex;
        flex-direction: row;
    }

    .td.payment svg,
    .td.payment path,
    .td.payment label {
        height: 100%;
        align-self: center;
        justify-content: center;
        vertical-align: middle;
    }

    .td.payment form button,
    .td.payment form select,
    .td.payment form input,
    .td.payment span,
    .td.payment i {
        font-style: normal;
        color: var(--text-primary);
    }

    .td.payment form select,
    .td.payment form input,
    .td.payment span,
    .td.payment i {
        padding: 1em 0.5em;
    }

    .td.payment form button {
        border-radius: 0 15px 15px 0;
    }

    .td.payment form select,
    .td.payment.ok i {
        border-radius: 15px 0 0 15px;
    }

    .td.payment form > input[type="number"],
    .td.payment form > span {
        flex: 2 !important;
    }

    input {
        background-color: var(--background-secondary);
        border: none;
        color: var(--text-secondary);
        align-self: center;
        text-align: center;
        /*width: 50%;*/
    }

    button {
        cursor: pointer;
    }

    .mobile {
        display: none;
    }

    @media screen and (width <= 1000px) {
        .desktop {
            display: none !important;
        }

        .mobile {
            display: block;
        }

        #filterHolder {
            display: flex;
            flex-direction: column;
        }

        #filterHolder input,
        #filterHolder :global(#locationSelect) {
            width: 100% !important;
            display: block
        }

        .mobile .td {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            width: 100%;
        }

        .bold {
            font-weight: bold;
        }

        .horizontal {
            display: flex;
            flex-direction: row;
        }

        .horizontal span + span {
            text-align: right;
            min-width: 0;
            overflow-wrap: anywhere;
        }

        .payment {
            display: block !important;
        }
    }
</style>
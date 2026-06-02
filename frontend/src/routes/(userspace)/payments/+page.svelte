<script lang="ts">
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {enhance} from "$app/forms"
    import {locations} from "$lib/stores/locations.svelte";
    import {onMount, untrack} from "svelte";

    const {data, form} = $props();

    let members = $state(data.payments ?? []);
    let filteredMembers = $state(members)

    onMount(() => {
        locations.load(true);
    })

    $effect(() => {
        members = data.payments.sort((a, b) => a.member.uuid.localeCompare(b.member.uuid)) ?? [];
    })

    $effect(() => {
        let result = members;

        if (selectedLocation != null) {
            result = result.filter((m) => {
                return m.member.location.id == selectedLocation?.id
            })
        }


        const search = memberTextFilter;
        if (search.length >= 3) {
            result = result.filter((m) => {
                return (
                    m.member.name?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.surname?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.email?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.phoneNumber?.toLowerCase().includes(search.toLowerCase()) ||
                    m.member.comment?.toLowerCase().includes(search.toLowerCase())
                )
            })
        }

        untrack(() => {
            filteredMembers = result;
        })
    })

    let memberTextFilter = $state("");
    let selectedLocation = $state(null);
    let selectedCategory = $state(-1);
    let showEntryFee = $state(false);

    const monthNames = ["Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"]

    const currentMonth = new Date().getMonth() + 1;
    const currentYear = new Date().getFullYear();

    const monthString = (month: number) => {
        return month.toString().length == 1 ? "0" + month.toString() : month.toString();
    }

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
                    .map(m => [parseInt(m.month.split("-")[1]), m])
            )
        ])
    );

</script>
<svelte:head>
    <title>Baza - Płatności</title>
</svelte:head>


{#snippet payment(payment, type, month, year, payerUuid)}
    {#if payment}
        <abbr class="td payment ok" title={payment.comment} style="font-style: unset; text-decoration: unset">
            <form action="?/deletePayment" method="post" use:enhance>
                {#if payment.paymentmethod == "cash"}
                    <i>💵</i>
                {:else}
                    <i>💳</i>
                {/if}
                <input type="hidden" name="paymentUuid" value={payment.uuid}>
                <span>{payment.amount}</span>
                <button type="submit" aria-label="usuń"
                        class="p-2! text-2xl! font-bold! text-neutral-700! hover:text-black! duration-200">
                    X
                </button>
            </form>
        </abbr>
    {:else}
        <div class="td payment bad">
            <form action="?/addPayment" method="POST" use:enhance>
                <input type="hidden" name="paymentType" value={type}>
                <input type="hidden" name="month" value={month}>
                <input type="hidden" name="year" value={year}>
                <input type="hidden" name="payerUuid" value={payerUuid}>
                <select name="paymentMethod" class="bg-transparent!">
                    <option value="CASH">💵</option>
                    <option value="DEBIT">💳</option>
                </select>
                <input type="number" name="amount" value={0} required min="0" max="1000" class="bg-transparent!">
                <label for={`show-comment-${payerUuid}-${month}-${year}`} class="bg-transparent!">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30"
                         class="cursor-pointer">
                        <path d="M544 128C544 110.3 529.7 96 512 96L128 96C110.3 96 96 110.3 96 128C96 145.7 110.3 160 128 160L512 160C529.7 160 544 145.7 544 128zM544 384C544 366.3 529.7 352 512 352L128 352C110.3 352 96 366.3 96 384C96 401.7 110.3 416 128 416L512 416C529.7 416 544 401.7 544 384zM96 256C96 273.7 110.3 288 128 288L512 288C529.7 288 544 273.7 544 256C544 238.3 529.7 224 512 224L128 224C110.3 224 96 238.3 96 256zM544 512C544 494.3 529.7 480 512 480L128 480C110.3 480 96 494.3 96 512C96 529.7 110.3 544 128 544L512 544C529.7 544 544 529.7 544 512z"/>
                    </svg>
                    <input type="checkbox" id={`show-comment-${payerUuid}-${month}-${year}`}
                           class="comment-show hidden peer">
                    <div
                            class="fixed inset-0 bg-black/40 opacity-0 pointer-events-none
           transition-opacity peer-checked:opacity-100
           peer-checked:pointer-events-auto"
                    >
                        <div class="bg-(--color-background-primary) absolute top-1/2 left-1/2 -translate-x-1/2 translate-y-[-200%] flex flex-col gap-5 pb-2! md:w-1/4!:w-full! rounded-md! h-fit border-(--color-border) border-2">
                                <textarea name="comment"
                                          class="bg-(--color-background-secondary) block rounded-md p-2! h-20!">{payment?.comment}</textarea>
                            <label for={`show-comment-${payerUuid}-${month}-${year}`}
                                   class="block text-gray-200 bg-(--background-special) rounded-sm text-2xl cursor-pointer p-1! hover:bg-neutral-800 duration-200">Zapisz</label>
                        </div>
                    </div>
                </label>
                <button type="submit" aria-label="Zapisz" class="bg-transparent!">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30"
                         class="cursor-pointer">
                        <path d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/>
                    </svg>
                </button>
            </form>
        </div>
    {/if}
{/snippet}

<div id="filterHolder">
    <span>Znajdź:</span>
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
    <select bind:value={selectedCategory} class="bg-neutral-300 rounded-lg text-neutral-600 px-5! py-1!">
        <option value={-1}>Wszystkie</option>
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

<div class="tabletop desktop">
    <div class="thead">
        <div class="tr">
            <div class="td">Imię</div>
            <div class="td">Nazwisko</div>
            <div class="td">Lokalizacja</div>
            <div class="td">Cena/mieś.</div>
            {#if showEntryFee}
                <div class="td">Wpisowe</div>
            {/if}
            <div class="td">{monthNames[currentMonth - 3]}</div>
            <div class="td">{monthNames[currentMonth - 2]}</div>
            <div class="td">{monthNames[currentMonth - 1]}</div>
        </div>
    </div>
    <div class="tbody">
        {#each filteredMembers as member (member.member.uuid)}
            {#if selectedCategory == null || selectedCategory == -1 || member.member.categories.some(a => a.id == selectedCategory)}
                <div class="tr outline-2 outline-transparent hover:outline-neutral-400! duration-300! rounded-xl delay-150">
                    <div class="td">{member.member.name != "" ? member.member.name : "- -"}</div>
                    <div class="td">{member.member.surname != "" ? member.member.surname : "- -"}</div>
                    <div class="td">{member.member.location.shortname}</div>
                    <div class="td">{member.member.monthlyFee * Number(multiplierMap.get(member.member.location.id)?.get(currentMonth)?.multiplier ?? 1)}</div>
                    {#if showEntryFee}
                        {@render payment(
                            member.payments.find((a) => a.month == null),
                            "STARTING_FEE",
                            null,
                            null,
                            member.member.uuid
                        )}
                    {/if}
                    {#each [2, 1, 0] as i}
                        {@render payment(
                            member.payments.find((a) => a.month == `${currentYear}-${monthString(currentMonth - i)}-01`),
                            "MONTHLY_FEE",
                            currentMonth - i > 0 ? currentMonth - i : currentMonth - i + 12,
                            currentMonth - i > 0 ? currentYear : currentYear - 1,
                            member.member.uuid
                        )}
                    {/each}
                </div>
            {/if}
        {/each}
    </div>
</div>
{#each filteredMembers as member (member.member.uuid)}
    <div class="mobile"
         style="outline: 2px solid var(--color-border); padding: 20px; border-radius: 15px; margin: 15px 0">
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
        <div class="horizontal"><span class="bold flex-1 flex items-center">Wpisowe</span><span
                class="flex-3">{@render payment(
            member.payments.find((a) => a.month == null),
            "STARTING_FEE",
            null,
            null,
            member.member.uuid
        )}</span></div>
        {#each [2, 1, 0] as i}
            <div class="horizontal flex"><span
                    class="bold flex-1 flex items-center">{monthNames[currentMonth - i - 1]}</span><span class="flex-3">
                {@render payment(
                    member.payments.find((a) => a.month == `${currentYear}-${monthString(currentMonth - i)}-01`),
                    "MONTHLY_FEE",
                    currentMonth - i > 0 ? currentMonth - i : currentMonth - i + 12,
                    currentMonth - i > 0 ? currentYear : currentYear - 1,
                    member.member.uuid
                )}
                </span></div>
        {/each}
    </div>
{/each}
<style>

    @import url('https://fonts.googleapis.com/css2?family=Noto+Color+Emoji&family=Noto+Emoji:wght@300..700&display=swap');
    @import "tailwindcss";

    * {
        font-weight: normal;
        font-family: 'Ubuntu', sans-serif, "Noto Color Emoji", sans-serif;
        font-optical-sizing: auto;
    }

    #filterHolder {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        width: 100%;
        padding: 10px;
        margin: 0 0 10px 0;
    }

    input {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 15px !important;
    }

    svg {
        fill: var(--color-text-secondary);
    }

    button:hover svg,
    svg:hover {
        fill: var(--color-text-primary);
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
        border-spacing: 0;
        width: 100%;
    }

    .thead {
        outline: var(--color-border) solid 2px;
        border-radius: 15px;
        /*height: 45px;*/
        display: table-header-group;
        position: sticky;
        top: 0;
        background-color: var(--color-background-primary);
    }

    .thead .td {
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
        min-width: 0;
        display: table-cell !important;
    }

    .td.payment {
        display: table-cell;
    }

    .td.payment.ok span {
        text-align: center;
        color: green;
    }

    .td.payment.bad form {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
    }

    .td.payment.bad input {
        color: red;
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
        color: var(--color-text-primary);
        background-color: var(--color-background-secondary);
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
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
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
        }

        .mobile .td.payment,
        .mobile .td.payment form {
            width: 100% !important;
            max-width: 100% !important;
        }
    }
</style>
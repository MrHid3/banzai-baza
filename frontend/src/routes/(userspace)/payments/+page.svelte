<script lang="ts">
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {enhance} from "$app/forms"
    import {locations} from "$lib/stores/locations.svelte";
    import {onMount, untrack} from "svelte";

    const {data, form} = $props();

    let members = $state(data.payments ?? []);
    let filteredMembers = $state(members)

    onMount(() => {
        locations.load();
    })

    $effect(() => {
        members = data.payments ?? [];
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
                    m.member.surnname?.toLowerCase().includes(search.toLowerCase()) ||
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

    const startingFees = $derived(new Map(
        members.map(m => [
            m.member.uuid,
            m.payments.find(a => a.paymentType === "STARTING_FEE")
        ])
    ));

    const paymentsByMonth = $derived(new Map(
        members.map(m => [
            m.member.uuid,
            new Map(
                m.payments
                    .filter(a => a.paymentType === "MONTHLY_FEE")
                    .map(a => [a.month, a])
            )
        ])
    ));

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


{#snippet payment(payment, type, month, year, payerUuid)}
    {#if payment}
        <td class="payment ok">
            <form action="?/deletePayment" method="POST" use:enhance>
                {#if payment.paymentMethod == "CASH"}
                    <i>💵</i>
                {:else}
                    <i>💳</i>
                {/if}
                <input type="hidden" name="uuid" value={payment.uuid}>
                <span>{payment.amount}</span>
                <button type="submit" aria-label="Usuń">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor"
                         class="bi bi-trash3-fill"
                         viewBox="0 0 16 16">
                        <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                    </svg>
                </button>
            </form>
        </td>
    {:else}
        <td class="payment bad">
            <form action="?/addPayment" method="POST" use:enhance>
                <input type="hidden" name="paymentType" value={type}>
                <input type="hidden" name="month" value={month}>
                <input type="hidden" name="year" value={year}>
                <input type="hidden" name="payerUuid" value={payerUuid}>
                <select name="paymentMethod">
                    <option value="CASH">💵</option>
                    <option value="DEBIT">💳</option>
                </select>
                <input type="number" name="amount" value={0}>
                <button type="submit" aria-label="Zapisz">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30">
                        <path d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/>
                    </svg>
                </button>
            </form>
        </td>
    {/if}
{/snippet}

<div id="filterHolder">
    <span>Znajdź:</span>
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
    <label class="desktop" for="showEntryFeeCheckbox">Pokaż wpisowe</label>
    <input bind:checked={showEntryFee} class="desktop" id="showEntryFeeCheckbox" type="checkbox">

    {#if form?.error}
        <span class="error">{form.error}</span>
    {/if}
</div>

<table>
    <thead class="desktop">
    <tr>
        <td>Imię</td>
        <td>Nazwisko</td>
        <td>Lokalizacja</td>
        <td>Cena/mieś.</td>
        {#if showEntryFee}
            <td>Wpisowe</td>
        {/if}
        <td>{monthNames[currentMonth - 3]}</td>
        <td>{monthNames[currentMonth - 2]}</td>
        <td>{monthNames[currentMonth - 1]}</td>
    </tr>
    </thead>
    <tbody>
    {#each filteredMembers as member (member.member.uuid)}
        <tr class="desktop">
            <td>{member.member.name}</td>
            <td>{member.member.surname}</td>
            <td>{member.member.location.shortname}</td>
            <td>{member.member.monthlyFee * Number(multiplierMap.get(member.member.location.id)?.get(currentMonth)?.multiplier ?? 1)}</td>
            {#if showEntryFee}
                {@render payment(
                    startingFees.get(member.member.uuid),
                    "STARTING_FEE",
                    null,
                    null,
                    member.member.uuid
                )}
            {/if}
            {#each [2, 1, 0] as i}
                {@render payment(
                    paymentsByMonth.get(member.member.uuid)?.get(resolveMonthKey(i)),
                    "MONTHLY_FEE",
                    currentMonth - i > 0 ? currentMonth - i : currentMonth - i + 12,
                    currentMonth - i > 0 ? currentYear : currentYear - 1,
                    member.member.uuid
                )}
            {/each}
        </tr>

    {/each}
    </tbody>
</table>
{#each filteredMembers as member (member.member.uuid)}
<div class="mobile" style="outline: 2px solid var(--color-border); padding: 20px; border-radius: 15px; margin: 15px 0">
    <div class="horizontal"><span class="bold flex-1">Imię</span><span class="text-right flex-1 block">{member.member.name}</span></div>
    <div class="horizontal"><span class="bold flex-1">Nazwisko</span><span class="text-right flex-1 block">{member.member.surname}</span></div>
    {#if !selectedLocation}
        <div class="horizontal"><span
                class="bold flex-1">Lokalizacja</span><span class="text-right flex-1 block">{member.member.location.shortname}</span></div>
    {/if}
    <div class="horizontal"><span class="bold flex-1">Cena/mieś.</span><span class="text-right flex-1 block">{member.member.monthlyFee}</span></div>
    <div class="horizontal"><span class="bold flex-1 flex items-center">Wpisowe</span><span class="flex-3">{@render payment(
        startingFees.get(member.member.uuid),
        "STARTING_FEE",
        null,
        null,
        member.member.uuid
    )}</span></div>
    {#each [2, 1, 0] as i}
        <div class="horizontal flex"><span class="bold flex-1 flex items-center">{monthNames[currentMonth - i - 1]}</span><span class="flex-3">
                {@render payment(
                    paymentsByMonth.get(member.member.uuid)?.get(resolveMonthKey(i)),
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

    @import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css";
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
    }

    table {
        border-spacing: 0 10px;
    }

    input {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 15px !important;
    }

    thead {
        outline: var(--color-border) solid 2px;
        border-radius: 15px;
        margin-bottom: 200px !important;
        padding: 10px;
        height: 45px;
    }

    thead td {
        text-transform: math-auto;
    }

    td {
        text-align: center;
        padding: 5px;
        margin: 10px;
        max-width: 10%;
        width: 10%;
    }

    td.payment {
        max-width: 20% !important;
    }

    td.payment input {
        width: 100% !important;
        display: block;
    }

    td.payment.ok span {
        text-align: center;
        color: green;
    }

    td.payment.bad form * {
        background-color: var(--color-background-primary);
    }

    td.payment.bad form {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
    }

    td.payment.bad input {
        color: red;
    }

    td.payment form {
        display: flex;
        flex-direction: row;
    }

    td.payment form > * {
        flex: 1;
        width: fit-content;
        height: 50px;
        text-align: center;
        border: none;
    }

    td.payment form button,
    td.payment form select,
    td.payment form input,
    td.payment span,
    td.payment i {
        font-style: normal;
        color: var(--color-text-primary);
        background-color: var(--color-background-secondary);
    }


    svg {
        fill: var(--color-text-secondary);
    }

    button:hover svg,
    svg:hover {
        fill: var(--color-text-primary);
    }

    td.payment form select,
    td.payment form input,
    td.payment span,
    td.payment i {
        padding: 1em 0.5em;
    }

    option,
    select {
        text-align: center !important;
    }

    input:focus {
        border: none;
        outline: none;
    }

    input::selection {
        border: none;
    }

    td.payment form button {
        border-radius: 0 15px 15px 0;
    }

    td.payment form select,
    td.payment.ok i {
        border-radius: 15px 0 0 15px;
    }

    td.payment form input,
    td.payment form span {
        flex: 2 !important;
    }

    input {
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
        display: inline-block !important;
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

        .mobile td {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            width: 100%;
        }

        .bold{
            font-weight: bold;
        }

        .horizontal{
            display: flex;
            flex-direction: row;
        }

        .horizontal span+span{
            text-align: right;
        }

        .mobile td.payment,
        .mobile td.payment form{
            width: 100% !important;
            max-width: 100% !important;
        }
    }
</style>
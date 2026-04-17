<script lang="ts">

    import LocationSelect from "$lib/LocationSelect.svelte";
    import {enhance} from "$app/forms"

    const {data, form} = $props();

    let members = $state(data.payments ?? []);
    let filteredMembers = $state(members)

    $effect(() => {
        members = data.payments ?? [];
    })

    $effect(() => {
        let result = members;
        if (selectedLocation != null) {
            result = result.filter((a) => a.member.location.id == selectedLocation?.id);
        }
        if (memberTextFilter.length >= 3) {
            result = result.filter((a) =>
                (a.member.name != null && a.member.name.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                (a.member.surname != null && a.member.surname.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                a.member.email.toLowerCase().includes(memberTextFilter.toLowerCase()) ||
                (a.member.phoneNumber != null && a.member.phoneNumber.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                (a.member.comment != null && a.member.comment.toLowerCase().includes(memberTextFilter.toLowerCase())));
        }

        filteredMembers = result;
    })

    let memberTextFilter = $state("");
    let selectedLocation = $state(null);
    let showEntryFee = $state(false);

    const monthNames = ["Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"]

    const currentMonth = (new Date().getMonth() + 1) % 12;
    const currentYear = new Date().getFullYear();

    const monthString = (month: number) => {
        return month.toString().length == 1 ? "0" + (month).toString() : (month).toString();
    }

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
                <button type="submit" class="fa-solid fa-x"></button>
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

                <button type="submit">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30">
                        <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                        <path
                                d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/>
                    </svg>
                </button>
            </form>
        </td>
    {/if}
{/snippet}

<meta charset="UTF-8">

<div id="filterHolder">
    <!--    TODO: dodaj lupe-->
    <span>Znajdź:</span>
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
    <label for="showEntryFeeCheckbox">Pokaż wpisowe</label>
    <input bind:checked={showEntryFee} id="showEntryFeeCheckbox" type="checkbox">

    {#if form?.error}
        <span class="error">{form.error}</span>
    {/if}
</div>
<table>
    <thead>
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
    {#each filteredMembers as member, index (index)}
        <tr>
            <td>{member.member.name}</td>
            <td>{member.member.surname}</td>
            <td>{member.member.location.shortname}</td>
            <td>{member.member.monthlyFee}</td>
            {#if showEntryFee}
                {@render payment(
                    member.payments.find((a) => a.paymentType == "STARTING_FEE"),
                    "STARTING_FEE",
                    null,
                    null,
                    member.member.uuid
                )}
            {/if}

            {#each [2, 1, 0] as i, index(index)}
                {@render payment(
                    member.payments.find((a) => a.month == `${currentYear}-${monthString(currentMonth - i)}-01`),
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

<style>

    @import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css";
    @import url('https://fonts.googleapis.com/css2?family=Noto+Color+Emoji&family=Noto+Emoji:wght@300..700&display=swap');

    * {
        font-weight: normal;
        font-family: 'Ubuntu', sans-serif, "Noto Color Emoji", sans-serif;
        font-optical-sizing: auto;
    }

    table {
        font-size: 1.2rem;
        line-height: 2.4rem;
    }

    thead {
        outline: var(--color-border) solid 2px;
        border-radius: 15px;
        margin-bottom: 200px !important;
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
        color: lime;
        /*padding: 5px 0 0 15px;*/
        padding: 5px 0;
        padding-left: 5%;
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
        fill: var(--color-text-primary);
        color: var(--color-text-primary);
        background-color: var(--color-background-secondary);
        padding: 5px 15px;
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
        padding: 5px;
        /*width: 50%;*/
    }

    button {
        cursor: pointer;
    }
</style>
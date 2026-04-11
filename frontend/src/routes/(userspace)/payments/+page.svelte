<script lang="ts">

    import LocationSelect from "$lib/LocationSelect.svelte";

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

    const monthNames = ["Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"]

    const currentMonth = new Date().getMonth().toString().length == 1 ? "0" + (new Date().getMonth() + 1) : new Date().getMonth() + 1;
    const currentYear = new Date().getFullYear();

</script>

<div id="filterHolder">
    <!--    TODO: dodaj lupe-->
    <span>Znajdź:</span>
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
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
        <td>Wpisowe</td>
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
            {#if member.payments.some((a) => a.paymentType == "STARTING_FEE")}
                <td class="payment-ok">{member.payments.find((a) => a.paymentType == "STARTING_FEE")?.amount}</td>
            {:else}
                <td class="payment-bad">
                    <input type="number" value={0}>
                </td>
            {/if}
            {#if member.payments.some((a) => a.month == `${currentYear}-${currentMonth - 2}`)}
                <td class="payment-ok">{member.payments.find((a) => a.month == `${currentYear}-${currentMonth - 2}`)?.amount}</td>
            {:else}
                <td class="payment-bad">
                    <input type="number" value={0}>
                </td>
            {/if}
            {#if member.payments.some((a) => a.month == `${currentYear}-${currentMonth - 1}`)}
                <td class="payment-ok">{member.payments.find((a) => a.month == `${currentYear}-${currentMonth - 1}`)?.amount}</td>
            {:else}
                <td class="payment-bad">
                    <input type="number" value={0}>
                </td>
            {/if}
            {#if member.payments.some((a) => a.month == `${currentYear}-${currentMonth}`)}
                <td class="payment-ok">{member.payments.find((a) => a.month == `${currentYear}-${currentMonth}`)?.amount}</td>
            {:else}
                <td class="payment-bad">
                    <input type="number" value={0}>
                </td>
            {/if}
        </tr>
    {/each}
    </tbody>
</table>

<style>
    table {
        font-size: 1.2rem;
        line-height: 2.4rem;
    }

    thead {
        outline: var(--color-border) solid 2px;
    }

    thead td {
        text-transform: math-auto;
    }

    td {
        text-align: center;
        padding: 5px;
        margin: 10px;
        max-width: 15% !important;
        width: 10%;
    }

    td.payment-ok {
        color: green;
    }

    td.payment-bad input{
        color: red;
    }

    input {
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
        display: inline-block !important;
        align-self: center;
        text-align: center;
        padding: 5px;
    }
</style>
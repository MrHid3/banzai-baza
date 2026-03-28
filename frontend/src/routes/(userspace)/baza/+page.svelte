<script lang="ts">
    import Member from "./Member.svelte";
    import Modal from "$lib/Modal.svelte";

    let {data} = $props();

    let members = $state(data.members);
    let filteredMembers = $state(members)

    $effect(() => {
        let result = members;
        if (selectedLocationId != -1) {
            result = result.filter((a) => a.location.id == selectedLocationId);
        }
        if (memberTextFilter.length >= 3) {
            result = result.filter((a) =>
                (a.name != null && a.name.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                (a.surname != null && a.surname.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                a.email.toLowerCase().includes(memberTextFilter.toLowerCase()) ||
                (a.phoneNumber != null && a.phoneNumber.toLowerCase().includes(memberTextFilter.toLowerCase())) ||
                (a.comment != null && a.comment.toLowerCase().includes(memberTextFilter.toLowerCase())));
        }

        filteredMembers = result;
    })

    let memberTextFilter = $state("");
    let selectedLocationId = $state(-1);

    let showDeleteModal = $state(false);
    let userToDeleteName = $state("");

    let showAddModal = $state(false);
    let addName = $state("");
    let addSurname = $state("");
    let addEmail = $state("");
    let addPhoneNumber = $state("");
    let addLocation = $state(1);
    let addMonthlyFee = $state(0);
    let comment = $state("");
</script>

<svelte:head>
    <title>Baza klubu</title>
</svelte:head>

<Modal bind:showModal={showDeleteModal}>
    {#snippet header()}
        <h2>Usunąć {userToDeleteName}?</h2>
    {/snippet}
    Nie można tego odwrócić
</Modal>

<div class="filterHolder">
<!--    TODO: dodaj lupe-->
    <span>Znajdź:</span>
    <input type="text" bind:value={memberTextFilter} id="textFilterInput"/>
    <span>Filtruj po lokalizacji:</span>
    <select name="locations" id="locationSelect" bind:value={selectedLocationId}>
        <option value={-1} selected>Wszystkie</option>
        {#each data.locations as location, index(index)}
            <option value={location.id}>{location.name}</option>
        {/each}
    </select>
</div>
<div class="membersTable" style="--number-of-elements-minus-four: {members.length + 1 - 4}">
    <div class="header">
        <span class="data">Imię</span>
        <span class="data">Nazwisko</span>
        <span class="data">Email</span>
        <span class="data">Nr telefonu</span>
        <span class="data">Lokalizacja</span>
        <span class="data">Cena/mieś.</span>
        <span class="data">Komentarz</span>
        <span class="data"></span>
        <button class="data" onclick={() => showAddModal = !showAddModal}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512" class="plusSvg">
                <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                <path d="M256 64c0-17.7-14.3-32-32-32s-32 14.3-32 32l0 160-160 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l160 0 0 160c0 17.7 14.3 32 32 32s32-14.3 32-32l0-160 160 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-160 0 0-160z"/>
            </svg>
        </button>
    </div>
    {#if showAddModal}
            <form action="">
                <span class="data"><input type="text" name="name">     </span>
                <span class="data"><input type="text" name="surname">  </span>
                <span class="data"><input type="text" name="email">    </span>
                <span class="data"><input type="text" name="phoneNumber"></span>
                <span class="data"><select name="location">
                {#each data.locations as location, index(index)}
                    <option value={location.id}>{location.shortname}</option>
                {/each}
                </select></span>
                <span class="data"><input type="number" name="monthlyFee"></span>
                <span class="data"><textarea name="comment"></textarea></span>
            </form>
    {/if}
    {#each filteredMembers as member, index (index)}
        <Member componentClass="Member" member={member}></Member>
    {/each}
</div>
{#if filteredMembers.length == 0}
    <div class="noResults">
        Brak wyników
    </div>
{/if}

<style>
    *{
        padding: 0;
        margin: 0;
        box-sizing: border-box;
    }

    /*textarea{*/
        /*height: 100% !important;*/
        /*font-size: 0.6em !important;*/
        /*height: 2.4em;*/
        /*display: block;*/
        /*font-size: 1.2em !important;*/
        /*display: block;*/
        /*height: 2.4em;        !* match your line-height *!*/
        /*resize: none;*/
        /*overflow: hidden;*/
    /*}*/

    form select, form input, form textarea{
        width: 100%;
        /*text-align: center;*/
        font-size: 1.4rem;
        /*padding: 5px 20px;*/
        /*height: 24px;*/
        height: 100%;
        line-height: 24px;
        align-self: center;
        /*padding: 10px;*/
        /*height: 100%;*/
    /*    display: block;*/
        text-align: center;
    }

    form select{
        /*padding: 0 !important;*/
    }

    form option{
        /*height: 100% !important;*/
        padding: 0 !important;
    }

    form textarea {
        /*height: 1.2em;*/
        /*max-height: 1.2em;*/
        margin: 0;
        overflow: hidden;
        vertical-align: middle;
        line-height: 1rem;
        /*display: block;*/
        resize: vertical;
        width: 100%;
    }

    form{
        width: 100%;
        max-width: 100%;
        display: table-row !important;
    }

    button.data{
        background-color: transparent;
        display: inline;
        border: none;
        vertical-align: middle;
    }

    .noResults {
        width: 100%;
        text-align: center;
        font-size: 2em;
        padding: 10px;
    }

    .filterHolder *{
        padding: 5px;
        vertical-align: middle;
    }

    .membersTable {
        display: table;
        text-align: center;
        line-height: 2.4em;
        font-size: 1.2em;
        margin: 0 auto;
        width: 100%;
    }

    .data:not(:has(*)), button.data {
        padding: 5px 20px;
    }

    .data {
        display: table-cell !important;
        width: auto;
        max-width: 20%;
        padding: 5px 5px;
    }

    .header {
        display: table-row;
        position: relative;
        width: 100%;
        outline-color: var(--color-border);
        outline-style: solid;
        outline-width: 2px;
    }

    .data:has(.plusSvg){
        position: relative;
    }

    .plusSvg{
        position: absolute;
        top: 50%;
        left: -70%;
        transform: translate(-50%, -50%);
        width: 30px;
        height: 30px;
        fill: var(--color-text-primary);
    }

    .filterHolder {
        position: relative;
        display: flex;
        flex-direction: row;
    }

    input, select, option, textarea{
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
        display: inline-block !important;
        align-self: center;
    }
</style>

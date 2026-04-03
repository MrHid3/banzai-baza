<script lang="ts">
    import Member from "./Member.svelte";
    import Modal from "$lib/Modal.svelte";
    import {enhance} from "$app/forms";
    import LocationSelect from "$lib/LocationSelect.svelte";

    let {data} = $props();

    let members = $state(data.members ?? []);
    let filteredMembers = $state(members)

    $effect(() => {
        members = data.members ?? [];
    })

    $effect(() => {
        let result = members;
        if (selectedLocation != null) {
            result = result.filter((a) => a.location.id == selectedLocation.id);
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
    let selectedLocation = $state(null);

    let showDeleteModal = $state(false);
    let userToDeleteName = $state("");

    let showAddFragment = $state(false);

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
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
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
        <span class="data small"></span>
        <span class="data small">
            <div>
            <button onclick={() => showAddFragment = !showAddFragment}>
                <svg class="plusSvg" viewBox="0 0 448 512" xmlns="http://www.w3.org/2000/svg">
                    <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                    <path d="M256 64c0-17.7-14.3-32-32-32s-32 14.3-32 32l0 160-160 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l160 0 0 160c0 17.7 14.3 32 32 32s32-14.3 32-32l0-160 160 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-160 0 0-160z"/>
                </svg>
            </button>
            </div>
        </span>
    </div>
    {#if showAddFragment}
        <form action="?/add" method="POST" use:enhance autocomplete="off">
            <span class="data"><input type="text" name="name"></span>
            <span class="data"><input type="text" name="surname"></span>
            <span class="data"><input type="text" name="email"></span>
            <span class="data"><input type="text" name="phoneNumber"></span>
            <span class="data">
                <LocationSelect class="locationSelect"></LocationSelect>
            </span>
            <span class="data"><input type="number" name="monthlyFee" value="150"></span>
            <span class="data"><textarea name="comment"></textarea></span>
            <span class="data"><button type="submit">
                <svg class="plusSvg" viewBox="0 0 448 512" xmlns="http://www.w3.org/2000/svg">
                    <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                    <path d="M256 64c0-17.7-14.3-32-32-32s-32 14.3-32 32l0 160-160 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l160 0 0 160c0 17.7 14.3 32 32 32s32-14.3 32-32l0-160 160 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-160 0 0-160z"/>
                </svg>
            </button></span>
        </form>
    {/if}
    {#each filteredMembers as member, index (index)}
        <Member bind:member={members[index]}></Member>
    {/each}
</div>
{#if filteredMembers.length == 0}
    <div class="noResults">
        Brak wyników
    </div>
{/if}

<style>
    /*.filterHolder :global(.LocationSelect){}*/
    .filterHolder {
        height: 30px;
    }

    .filterHolder * {
        height: 100%;
    }

    form :global(.locationSelect) {
        width: 100%;
    }

    * {
        padding: 0;
        margin: 0;
        box-sizing: border-box;
    }

    .data > div {
        width: 100%;
        height: 100% !important;
        display: flex;
        justify-content: center;
        align-items: center;
        overflow: hidden;
    }

    form .data > *,
    form :global(.locationSelect) {
        font-size: 0.7em !important;
        height: fit-content;
        /*height: fit-content;*/
        line-height: 1.1em !important;
        width: 100% !important;
        vertical-align: middle !important;
        padding: 10px;
    }

    .small {
        max-width: 70px !important;
        width: 0 !important;
    }

    .data button {
        border-radius: 15px;
        background-color: var(--color-background-secondary);
        display: flex;
        align-items: center;
        justify-content: center;
        border: none;
        width: 50px;
        height: 50px !important;
        margin: 0 auto;
        align-content: center;
        padding: 10px;
    }

    button {
        cursor: pointer;
    }

    form textarea {
        resize: vertical;
        padding: 0 5px;
        overflow: hidden;
        /*font-size: 0.6em !important;*/
        /*height: 100% !important;*/
    }

    form {
        display: table-row;
    }

    .noResults {
        width: 100%;
        text-align: center;
        font-size: 2em;
        padding: 10px;
    }

    .filterHolder * {
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

    .data:not(:has(*)) {
        padding: 5px 20px;
    }

    .data {
        display: table-cell !important;
        width: auto;
        max-width: 20%;
        padding: 5px 5px;
        height: fit-content;
    }

    span.data {
        /*line-height: 0;*/
        padding: 10px;
        font-size: 1.2em;
    }

    .header {
        display: table-row;
        position: relative;
        width: 100%;
        outline-color: var(--color-border);
        outline-style: solid;
        outline-width: 2px;
    }

    .data:has(.plusSvg) {
        position: relative;
    }

    .plusSvg {
        /*position: absolute;*/
        /*top: 50%;*/
        /*left: -70%;*/
        /*transform: translate(-50%, -50%);*/
        width: 30px;
        height: 30px;
        fill: var(--color-text-primary);
        align-self: center;
    }

    .filterHolder {
        position: relative;
        display: flex;
        flex-direction: row;
    }

    input, textarea, form button {
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
        display: inline-block !important;
        align-self: center;
        text-align: center;
    }

    textarea {
        text-align: left;
    }
</style>

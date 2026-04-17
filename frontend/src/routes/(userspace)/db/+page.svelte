<script lang="ts">
    import Member from "./Member.svelte";
    import Modal from "$lib/Modal.svelte";
    import {enhance} from "$app/forms";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {untrack} from "svelte";

    let {data, form} = $props();

    let members = $state(data.members ?? []);
    let filteredMembers = $state(members)

    $effect(() => {
        members = data.members ?? [];
    })

    $effect(() => {
        let result = members;
        if (selectedLocation != null) {
            result = result.filter((a) => a.location.id == selectedLocation?.id);
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

    let showAddFragment = $state(false);

    const phonePattern = "(?:[+][0-9]{1,3} )?[0-9]{3}[\\- ]?[0-9]{3}[\\- ]?[0-9]{3,6}";

    let deleteQueue : string[] = $state([]);

    $effect(() => {
        if (form?.ok)
            if (form?.type == "delete") {
                const uuid : String = form?.uuid as String;
                untrack(() => {
                    deleteQueue = [...deleteQueue, uuid]
                })
            }else if (form?.type == "undelete") {
                const uuid = form?.uuid as String;
                untrack(() => {
                    const index = deleteQueue.indexOf(uuid);
                    deleteQueue.splice(index, 1);
                })
            }
    })

</script>

<svelte:head>
    <title>Baza klubu</title>
</svelte:head>

<div class="filterHolder">
    <!--    TODO: dodaj lupe-->
    <span>Znajdź:</span>
    <input bind:value={memberTextFilter} id="textFilterInput" type="text"/>
    <span>Filtruj po lokalizacji:</span>
    <LocationSelect all={true} bind:location={selectedLocation} short={false}></LocationSelect>
    {#if form?.error}
        <span class="error">{form.error}</span>
    {/if}
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
        <span class="data small">
            {#if deleteQueue.length > 0}
                   <form action="?/undelete" method="POST" use:enhance>
                       <input type="hidden" name="memberUuid" value={deleteQueue[deleteQueue.length - 1]}>
                       <button type="submit">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path d="M24 192l144 0c9.7 0 18.5-5.8 22.2-14.8s1.7-19.3-5.2-26.2l-46.7-46.7c75.3-58.6 184.3-53.3 253.5 15.9 75 75 75 196.5 0 271.5s-196.5 75-271.5 0c-10.2-10.2-19-21.3-26.4-33-9.5-14.9-29.3-19.3-44.2-9.8s-19.3 29.3-9.8 44.2C49.7 408.7 61.4 423.5 75 437 175 537 337 537 437 437S537 175 437 75C342.8-19.3 193.3-24.7 92.7 58.8L41 7C34.1 .2 23.8-1.9 14.8 1.8S0 14.3 0 24L0 168c0 13.3 10.7 24 24 24z"/></svg>
                       </button>
                   </form>
            {/if}
        </span>
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
            <span class="data"><input type="text" name="phoneNumber" pattern={phonePattern}></span>
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

    input:not(:focus):invalid {
        border: 1px solid red;
    }

    svg{
        fill: var(--color-text-primary);
    }
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

    span.data form{
        height: fit-content !important;
    }

    form .data > *,
    form :global(.locationSelect) {
        font-size: 0.7em !important;
        height: fit-content;
        /*height: fit-content;*/
        line-height: 1.1em !important;
        width: 100% !important;
        vertical-align: middle !important;
    }

    .small {
        max-width: 70px !important;
        width: 0 !important;
    }

    .data button {
        border-radius: 0 15px 15px 0 ;
        background-color: var(--color-background-secondary);
        display: flex;
        align-items: center;
        justify-content: center;
        border: none;
        width: 100%;
        height: 100% !important;
        padding: 10px;
        margin: 0 auto;
        align-content: center;
    }

    button {
        cursor: pointer;
    }

    form textarea {
        resize: vertical;
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
    }

    .filterHolder * {
        vertical-align: middle;
    }

    .membersTable {
        display: table;
        border-spacing: 0 15px;
        text-align: center;
        line-height: 2.4rem;
        font-size: 1.2em;
        margin: 0 auto;
        width: 100%;
    }

    .data:not(:has(*)) {
    }

    .data {
        display: table-cell !important;
        width: auto;
        max-width: 20%;
        height: fit-content;
    }

    span.data {
        /*line-height: 0;*/
        font-size: 1.2em;
    }

    .header {
        display: table-row;
        line-height: 0;
        /*position: relative;*/
        width: 100%;
        outline: var(--color-border) solid 2px;
        position: sticky;
        top: 10px;
        background-color: var(--color-background-primary);
        z-index: 10;
        border-radius: 15px;
    }

    .data:has(.plusSvg) {
        position: relative;
    }

    .plusSvg {
        /*position: absolute;*/
        /*top: 50%;*/
        /*left: -70%;*/
        /*transform: translate(-50%, -50%);*/
        width: 20px;
        height: 20px;
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

<script lang="ts">
    import Member from "./Member.svelte";
    import {enhance} from "$app/forms";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {untrack} from "svelte";

    let {data, form} = $props();

    let members = $state(data.members ?? []);

    $effect(() => {
        members = data.members ?? [];
    })

    let filteredMembers = $state(members);

    $effect(() => {
        let result = members;

        if(selectedLocation != null){
            result = result.filter((m) => {
                return m.location.id == selectedLocation.id
            })
        }

        const search = memberTextFilter;
        if(search.length >= 3){
            result = result.filter((m) => {
                return (
                    m.name?.toLowerCase().includes(search.toLowerCase()) ||
                    m.surnname?.toLowerCase().includes(search.toLowerCase()) ||
                    m.email?.toLowerCase().includes(search.toLowerCase()) ||
                    m.phoneNumber?.toLowerCase().includes(search.toLowerCase()) ||
                    m.comment?.toLowerCase().includes(search.toLowerCase())
                )
            })
        }

        untrack(() => {
            filteredMembers = result;
        })
    })

    let memberTextFilter = $state("");
    let selectedLocation = $state(null);

    let showAddFragment = $state(false);

    const phonePattern = "(?:[+][0-9]{1,3} )?[0-9]{3}[\\- ]?[0-9]{3}[\\- ]?[0-9]{3,6}";

    let deleteQueue: string[] = $state([]);

    $effect(() => {
        if (form?.ok)
            if (form?.type == "delete") {
                const uuid: String = form?.uuid as String;
                untrack(() => {
                    deleteQueue = [...deleteQueue, uuid]
                })
            } else if (form?.type == "undelete") {
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
    <div class="header desktop">
        <span class="data">Imię</span>
        <span class="data">Nazwisko</span>
        <span class="data">Email</span>
        <span class="data short">Nr telefonu</span>
        <span class="data short">Lokalizacja</span>
        <span class="data short">Cena/mieś.</span>
        <span class="data">Komentarz</span>
        <span class="data small">
            {#if deleteQueue.length > 0}
                   <form action="?/undelete" method="POST" use:enhance>
                       <input type="hidden" name="memberUuid" value={deleteQueue[deleteQueue.length - 1]}>
                       <button type="submit" class="left" aria-label="Od-usuń członka">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path
                                  d="M24 192l144 0c9.7 0 18.5-5.8 22.2-14.8s1.7-19.3-5.2-26.2l-46.7-46.7c75.3-58.6 184.3-53.3 253.5 15.9 75 75 75 196.5 0 271.5s-196.5 75-271.5 0c-10.2-10.2-19-21.3-26.4-33-9.5-14.9-29.3-19.3-44.2-9.8s-19.3 29.3-9.8 44.2C49.7 408.7 61.4 423.5 75 437 175 537 337 537 437 437S537 175 437 75C342.8-19.3 193.3-24.7 92.7 58.8L41 7C34.1 .2 23.8-1.9 14.8 1.8S0 14.3 0 24L0 168c0 13.3 10.7 24 24 24z"/></svg>
                       </button>
                   </form>
            {/if}
        </span>
        <span class="data small">
            <button class="right" onclick={(e) => { e.stopPropagation(); showAddFragment = !showAddFragment}} aria-label="Pokaż interfejs dodawania użytkownika">
                <svg class="plusSvg" viewBox="0 0 448 512" xmlns="http://www.w3.org/2000/svg">
                    <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                    <path d="M256 64c0-17.7-14.3-32-32-32s-32 14.3-32 32l0 160-160 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l160 0 0 160c0 17.7 14.3 32 32 32s32-14.3 32-32l0-160 160 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-160 0 0-160z"/>
                </svg>
            </button>
        </span>
    </div>
    {#if showAddFragment}
        <form action="?/add" method="POST" use:enhance autocomplete="off" class="addFragment">
            <span class="data"><input type="text" name="name"></span>
            <span class="data"><input type="text" name="surname"></span>
            <span class="data"><input type="text" name="email"></span>
            <span class="data short"><input type="text" name="phoneNumber" pattern={phonePattern}></span>
            <span class="data">
                <LocationSelect class="locationSelect"></LocationSelect>
            </span>
            <span class="data short"><input type="number" name="monthlyFee" value="150"></span>
            <span class="data"><textarea name="comment"></textarea></span>
            <span class="data"></span>
            <span class="data small">
                <button type="submit" class="both" aria-label="Dodaj członka">
                    <svg class="plusSvg" viewBox="0 0 448 512" xmlns="http://www.w3.org/2000/svg">
                        <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                        <path d="M256 64c0-17.7-14.3-32-32-32s-32 14.3-32 32l0 160-160 0c-17.7 0-32 14.3-32 32s14.3 32 32 32l160 0 0 160c0 17.7 14.3 32 32 32s32-14.3 32-32l0-160 160 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-160 0 0-160z"/>
                    </svg>
                </button>
            </span>
        </form>
    {/if}
    {#each filteredMembers as member (member.uuid)}
        <Member bind:member={members[members.findIndex(m => m.uuid === member.uuid)]}></Member>
<!--        <Member bind:member={member}></Member>-->
    {/each}
</div>
{#if filteredMembers.length == 0}
    <div class="noResults">
        Brak wyników
    </div>
{/if}

<style>
    .noResults{
        font-size: 2em;
        text-align: center;
    }

    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    .filterHolder {
        display: flex;
        flex-direction: row;
    }

    .membersTable {
        width: 100%;
        display: table;
        border-spacing: 0 10px;
        table-layout: fixed;
        /*border-collapse: collapse;*/
    }

    button.right {
        border-radius: 0 15px 15px 0 !important;
    }

    button.left {
        border-radius: 15px 0 0 15px !important;
        padding: 10px !important;
    }

    button.both {
        border-radius: 15px !important;
    }

    .header{
        position: sticky;
        top: 10px;
        left: 0;
        background-color: var(--color-background-primary);
        z-index: 2137;
    }

    .header,
    .filterHolder,
    .addFragment {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        display: table-row;
        width: 100%;
        padding: 10px;
    }

    .addFragment button {
        height: 100% !important;
        padding: 12px !important;
    }

    .header > *,
    .addFragment > * {
        display: table-cell;
        vertical-align: middle;
        max-width: 100%;
    }

    .addFragment input {
        color: var(--color-text-primary);
        /*font-size: 1.2em;*/
        max-width: 100%;
        width: 95%;
        text-align: center;
    }

    .short {
        width: 10%;
        table-layout: fixed;
    }

    .data:has(button) {
        width: 5%;
    }

    .header button,
    .addFragment button {
        border: none;
        background-color: var(--color-background-secondary);
        width: 100%;
        padding: 10px;
        height: 100%;
    }

    .data {
        text-align: center;
    }

    .data.small{
        table-layout: fixed;
        width: 5%;
    }

    svg {
        width: 20px;
        height: 20px;
        fill: var(--color-text-secondary);
        cursor: pointer;
        transition-duration: 0.6s;
    }

    button:hover svg,
    svg:hover{
        fill: var(--color-text-primary);
    }

    button {
        cursor: pointer !important;
    }

    input,
    :global(.locationSelect) {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 15px !important;
    }

    :not(.filterHolder) :global(.locationSelect) {
        padding: 0 20px;
    }

    .data:has(textarea){
        padding: 5px 0 ;
    }

    textarea {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 10px;
        color: var(--color-text-primary);
        padding: 0 5px;
        resize: vertical;
        width: 80%;
    }

    input{
        color: var(--color-text-secondary);
        text-align: center;
    }

    @media screen and (width <= 1000px){

        .desktop{
            display: none;
        }

        .filterHolder{
            display: flex;
            flex-direction: column;
        }

        .filterHolder input,
        .filterHolder :global(#locationSelect){
            width: 100% !important;
            display: block
        }
    }
</style>

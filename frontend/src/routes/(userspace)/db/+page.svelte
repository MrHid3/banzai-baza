<script lang="ts">
    import Member from "./Member.svelte";
    import {enhance} from "$app/forms";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {untrack} from "svelte";

    let {data, form} = $props();

    let members = $state(data.members ?? []);
    let categories = $derived(data.categories)

    $effect(() => {
        members = data.members?.sort((a, b) => a.uuid.localeCompare(b.uuid)) ?? [];
    })

    let filteredMembers = $state(members);

    $effect(() => {
        let result = members;

        if (selectedLocation != null) {
            result = result.filter((m) => {
                return m.location.id == selectedLocation.id
            })
        }

        const search = memberTextFilter;
        if (search.length >= 3) {
            result = result.filter((m) => {
                return (
                    m.name?.toLowerCase().includes(search.toLowerCase()) ||
                    m.surname?.toLowerCase().includes(search.toLowerCase()) ||
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

    let mobileEdit = $state(false);

    let deleteMode = $state(false);

    function triggerDelete() {
        if (deleteMode) {
            deleteMode = false;
        } else {
            if (!mobileEdit) {
                deleteMode = true;
            }
        }
    }

    function triggerEdit() {
        if (mobileEdit) {
            mobileEdit = false;
        } else {
            if (!deleteMode) {
                mobileEdit = true;
            }
        }
    }


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
<div class="mobile flex hover">
    <form action="?/undelete" method="POST" use:enhance>
        <input name="memberUuid" type="hidden" value={deleteQueue[deleteQueue.length - 1]}>
        <button aria-label="Od-usuń członka" class="left" style="{deleteQueue.length > 0 ? '' : 'pointer-events: none'}"
                type="submit">
            <svg style="fill: {deleteQueue.length > 0 ? 'var(--color-text-secondary)' : 'var(--color-background-secondary)'}"
                 viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg">
                <!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
                <path
                        d="M24 192l144 0c9.7 0 18.5-5.8 22.2-14.8s1.7-19.3-5.2-26.2l-46.7-46.7c75.3-58.6 184.3-53.3 253.5 15.9 75 75 75 196.5 0 271.5s-196.5 75-271.5 0c-10.2-10.2-19-21.3-26.4-33-9.5-14.9-29.3-19.3-44.2-9.8s-19.3 29.3-9.8 44.2C49.7 408.7 61.4 423.5 75 437 175 537 337 537 437 437S537 175 437 75C342.8-19.3 193.3-24.7 92.7 58.8L41 7C34.1 .2 23.8-1.9 14.8 1.8S0 14.3 0 24L0 168c0 13.3 10.7 24 24 24z"/>
            </svg>
        </button>
        <button aria-label="Tryb usuwania" onclick={() => triggerDelete()}
                style="{mobileEdit ? 'pointer-events: none;' : ''}{deleteMode ? 'background-color: var(--color-border)' : ''}"
                type="button">
            <svg class="bi bi-trash3-fill" height="30"
                 style="fill: {!mobileEdit ? 'var(--color-text-secondary)' : 'var(--color-background-secondary)'}"
                 viewBox="0 0 16 16"
                 width="30"
                 xmlns="http://www.w3.org/2000/svg">
                <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
            </svg>
        </button>
        <button aria-label="Tryb edycji" onclick={() => triggerEdit()}
                style="{deleteMode ? 'pointer-events: none;' : ''}{mobileEdit ? 'background-color: var(--color-border)' : ''}"
                type="button">
            <svg class="bi bi-pencil-square" fill="currentColor" height="30"
                 style="fill: {!deleteMode ? 'var(--color-text-secondary)' : 'var(--color-background-secondary)'}"
                 viewBox="0 0 16 16"
                 width="30"
                 xmlns="http://www.w3.org/2000/svg">
                <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                <path d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"
                      fill-rule="evenodd"/>
            </svg>
        </button>
    </form>
</div>
<div class="mobile flex">
    <form action="?/add" class="addForm" method="POST" use:enhance>
        <div class="horizontal"><span class="bold">Imię</span><span><input name="name" type="text"></span>
        </div>
        <div class="horizontal"><span class="bold">Nazwisko</span><span><input name="surname" type="text"
        ></span>
        </div>
        <div class="horizontal"><span class="bold">Email</span><span><input name="email" type="email"
        ></span>
        </div>
        <div class="horizontal"><span class="bold">Nr.tel</span><span><input name="phoneNumber" pattern={phonePattern}
                                                                             required
                                                                             type="text"
        ></span>
        </div>
        <div class="horizontal"><span class="bold">Lokalizacja</span><span><LocationSelect all={false}
                                                                                           class="locationSelect"></LocationSelect></span>
        </div>
        <div class="horizontal"><span class="bold">Cena/mieś</span><span><input max="1000" min="0" name="monthlyFee"
                                                                                type="number"></span>
        </div>
        <div class="horizontal"><span class="bold">Kategorie</span><span>
            <div class="categorySelect">
                {#each categories as category (category.id)}
                    <label class="categoryCheckbox">
                        <input type="checkbox" name="categories" value={category.id}>
                        <span>{category.shortname}</span>
                    </label>
                {/each}
            </div>
        </span></div>
        <div class="horizontal"><span
                class="bold">Komentarz</span><span><textarea name="comment"></textarea></span></div>
        <div class="horizontal">
            <span></span>
            <button class="save" type="submit">Dodaj</button>
        </div>
    </form>
</div>
<div class="membersTable desktop">
    <div class="header desktop">
        <span class="data">#</span>
        <span class="data">Imię</span>
        <span class="data">Nazwisko</span>
        <span class="data">Email</span>
        <span class="data short">Nr telefonu</span>
        <span class="data short">Lokalizacja</span>
        <span class="data short">Cena/mieś.</span>
        <span class="data short">Kategorie</span>
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
            <button aria-label="Pokaż interfejs dodawania użytkownika" class="right"
                    onclick={(e) => { e.stopPropagation(); showAddFragment = !showAddFragment}}>
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
            <span class="data short"><input type="text" name="phoneNumber" pattern={phonePattern} required></span>
            <span class="data">
                <LocationSelect class="locationSelect"></LocationSelect>
            </span>
            <span class="data short"><input type="number" name="monthlyFee" value="150"></span>
            <span class="data">
                <div class="categorySelect">
                    {#each categories as category (category.id)}
                        <label class="categoryCheckbox">
                            <input type="checkbox" name="categories" value={category.id}>
                            <span>{category.shortname}</span>
                        </label>
                    {/each}
                </div>
            </span>
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
        <Member bind:member={members[members.findIndex(m => m.uuid === member.uuid)]} mobileEdit={mobileEdit}
                deleteMode={deleteMode} categories={categories} num={members.findIndex(m => m.uuid == member.uuid) + 1}></Member>
        <!--        <Member bind:member={member}></Member>-->
    {/each}
</div>
<div class="mobile">
    {#each filteredMembers as member (member.uuid)}
        <Member bind:member={members[members.findIndex(m => m.uuid === member.uuid)]} mobileEdit={mobileEdit}
                deleteMode={deleteMode} categories={categories}></Member>
        <!--        <Member bind:member={member}></Member>-->
    {/each}
</div>
{#if filteredMembers.length == 0}
    <div class="noResults">
        Brak wyników
    </div>
{/if}

<style>
    .categorySelect {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: center;
        max-width: 100%;
        justify-content: space-evenly;
    }

    .categoryCheckbox {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        background-color: var(--color-background-secondary);
        border-radius: 8px;
        cursor: pointer;
        white-space: nowrap;
        user-select: none;
        transition: background-color 0.2s;
    }

    .categoryCheckbox input[type="checkbox"] {
        cursor: pointer;
        margin: 0;
        width: auto;
        accent-color: var(--color-text-primary);
    }

    .categoryCheckbox span {
        font-size: 0.85em;
    }

    .categoryCheckbox:has(input:checked) {
        background-color: var(--color-border);
    }

    .addForm {
        display: flex;
        flex-direction: column !important;
        gap: 5px;
        padding: 10px;
    }

    .addForm > div span + span {
        flex: 7;
    }

    .addForm > div span + span input,
    .addForm > div span + span :global(.locationSelect),
    .addForm > div span + span textarea {
        width: 100%;
    }

    .addForm > div span {
        flex: 4;
    }

    .noResults {
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

    .horizontal {
        display: flex;
        flex-direction: row;
    }


    button {
        border: none;
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

    .header {
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

    .bold {
        font-weight: bold;
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

    .data.small {
        table-layout: fixed;
        width: 5%;
    }

    svg {
        width: 20px;
        height: 20px;
        fill: var(--color-text-secondary);
        cursor: pointer;
        transition: fill 0.6s;
    }

    button:hover svg,
    svg:hover {
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

    .data:has(textarea) {
        padding: 5px 0;
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

    input {
        color: var(--color-text-secondary);
        text-align: center;
    }

    svg {
        width: 100%;
        display: block;
        align-self: center;
        justify-self: center;
    }

    .mobile {
        display: none;
    }

    @media screen and (width <= 1000px) {

        .mobile {
            display: block;
        }

        .flex {
            outline: 2px solid var(--color-border);
            margin: 10px 0;
            border-radius: 15px;
        }

        .hover {
            position: sticky;
            top: 10px;
        }

        .flex form {
            display: flex;
            justify-content: space-between;
            flex-direction: row;
            border: none;
            margin: 0;
            width: 100%;
            height: fit-content;
        }

        .flex form button {
            transition: background-color 0.4s;
        }

        .flex form button:last-child:not(.save) {
            border-radius: 0 15px 15px 0 !important;
        }

        .flex button {
            padding: 10px;
            background-color: var(--color-background-secondary);
            width: 100%;
        }

        .flex button,
        .flex form {
            flex: 1;
        }

        .desktop {
            display: none;
        }

        .filterHolder {
            display: flex;
            flex-direction: column;
        }

        .filterHolder input,
        .filterHolder :global(#locationSelect) {
            width: 100% !important;
            display: block
        }

        .save {
            border-radius: 15px !important;
        }

        .addForm .horizontal:has(.categorySelect) {
            flex-direction: column;
            align-items: flex-start;
        }

        .addForm .horizontal:has(.categorySelect) > span:last-child {
            width: 100%;
        }

        .categorySelect {
            width: 100%;
        }
    }
</style>
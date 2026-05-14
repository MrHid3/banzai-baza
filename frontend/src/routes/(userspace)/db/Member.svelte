<script lang="ts">
    import {applyAction, enhance} from "$app/forms";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {invalidate} from "$app/navigation";

    let {member = $bindable(), mobileEdit, deleteMode, categories}: {
        member: {
            uuid: string,
            name: string,
            surname: string,
            email: string,
            phoneNumber: string,
            monthlyFee: number,
            location: {
                name: string,
                shortname: string,
                id: number
            },
            comment: string,
            categories: [{
                id: number,
                name: string,
                shortname: string,
            }]
            // payments:
            //     {
            //         month: number,
            //         year: number,
            //         method: string,
            //         amount: number,
            //         active: boolean
            //     }[]
        },
        mobileEdit: boolean,
        deleteMode: boolean,
        categories: [{
            id: number,
            name: string,
            shortname: string
        }]
    } = $props();

    let edit = $state(false);

    const phonePattern = "(?:[+][0-9]{1,3} )?[0-9]{3}[\\- ]?[0-9]{3}[\\- ]?[0-9]{3,6}";

    async function deleteCategoryFromUser(memberUuid: string, categoryId: number) {
        const form = new FormData();
        form.append("memberUuid", memberUuid);
        form.append("categoryId", categoryId.toString());

        const response = await fetch("?/deleteCategoryFromUser", {
            method: "POST",
            body: form
        })

        const result = await response.json();
        await applyAction(result)
        await invalidate("/api/member")
    }


    async function addCategoryToUser(memberUuid: string, categoryId: number) {
        const form = new FormData();
        form.append("memberUuid", memberUuid);
        form.append("categoryId", categoryId.toString());

        const response = await fetch("?/addCategoryToUser", {
            method: "POST",
            body: form
        })

        const result = await response.json();
        await applyAction(result)
        await invalidate("/api/member")
    }

    let selectedCategoryId = categories[0]?.id ?? -1;
</script>

{#if !edit}
    <div class="row desktop">
        <span class="data">{member.name}</span>
        <span class="data">{member.surname}</span>
        <span class="data">{member.email}</span>
        <span class="data">{member.phoneNumber}</span>
        <span class="data">{member.location ? member.location.shortname : ""}</span>
        <span class="data">{member.monthlyFee}</span>
        <span class="data">
            {#each member.categories as category (category.id)}
                <span class="category">{category.shortname}</span>
            {/each}
        </span>
        <span class="data">
            <div class="textarea">
                <textarea readonly>{member.comment}
                </textarea>
            </div>
        </span>
        <span class="data">
            <button onclick={() => edit = true} class="left" aria-label="Edytuj">
                <svg fill="currentColor" viewBox="0 0 16 16"
                     xmlns="http://www.w3.org/2000/svg">
                    <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                    <path d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"
                          fill-rule="evenodd"/>
                </svg>
            </button>
        </span>
        <div class="data">
            <form action="?/delete" method="POST" use:enhance>
                <input type="hidden" name="memberUuid" value={member.uuid}>
                <button type="submit" title="delete" class="right" aria-label="Usuń">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                         viewBox="0 0 16 16">
                        <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                    </svg>
                </button>
            </form>
        </div>
    </div>
    <div class="mobile">
        {#if !mobileEdit}
            <!--        <div class="horizontal">-->
            <!--            <span>{member.name}</span>-->
            <!--            <span>{member.surname}</span>-->
            <!--            <span><button>Rozwiń</button></span>-->
            <!--        </div>-->
            <div class="horizontal"><span class="bold">Imię</span><span>{member.name}</span></div>
            <div class="horizontal"><span class="bold">Nazwisko</span><span>{member.surname}</span></div>
            <div class="horizontal"><span class="bold">Email</span><span>{member.email}</span></div>
            <div class="horizontal"><span class="bold">Nr. tel</span><span>{member.phoneNumber}</span></div>
            <div class="horizontal"><span class="bold">Lokalizacja</span><span>{member.location?.shortname}</span></div>
            <div class="horizontal"><span class="bold">Cena/mieś.</span><span>{member.monthlyFee}</span></div>
            <div class="horizontal"><span
                    class="bold">Komentarz</span><span><textarea>{member.comment}</textarea></span></div>
            {#if deleteMode}
                <div class="horizontal" style="flex-direction: row-reverse;">
                    <form action="?/delete" method="POST" use:enhance class="hidden" style="padding: 5px">
                        <input type="hidden" name="memberUuid" value={member.uuid}>
                        <button type="submit" title="delete" class="right" aria-label="Usuń">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                                 viewBox="0 0 16 16">
                                <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                            </svg>
                        </button>
                    </form>
                </div>
            {/if}
        {:else}
            <form action="?/update" method="POST" use:enhance>
                <div class="horizontal"><span class="bold">Imię</span><span><input type="text" bind:value={member.name}></span>
                </div>
                <div class="horizontal"><span class="bold">Nazwisko</span><span><input type="text"
                                                                                       bind:value={member.surname}></span>
                </div>
                <div class="horizontal"><span class="bold">Email</span><span><input type="email"
                                                                                    bind:value={member.email}></span>
                </div>
                <div class="horizontal"><span class="bold">Nr.tel</span><span><input type="text" required
                                                                                     pattern={phonePattern}
                                                                                     bind:value={member.phoneNumber}></span>
                </div>
                <div class="horizontal"><span class="bold">Lokalizacja</span><span><LocationSelect all={false}
                                                                                                   bind:location={member.location}
                                                                                                   class="locationSelect"></LocationSelect></span>
                </div>
                <div class="horizontal"><span class="bold">Cena/mieś</span><span><input type="number" min="0"
                                                                                        max="1000"
                                                                                        bind:value={member.monthlyFee}></span>
                </div>
                <div class="horizontal"><span
                        class="bold">Komentarz</span><span><textarea>{member.comment}</textarea></span></div>
                <div class="horizontal">
                    <span></span>
                    <button type="submit" class="save">Zapisz</button>
                </div>
            </form>
        {/if}
    </div>
{:else}
    <form action="?/update" method="POST" class="row desktop" use:enhance={() => {
           return({ result }) => {
               if (result.type === "success") {
                   edit = false;
           }
    }}}>
        <input type="hidden" name="memberUuid" bind:value={member.uuid}>
        <span class="data">
                <input type="text" name="name" bind:value={member.name}>
        </span>
        <span class="data">
                <input type="text" name="surname" bind:value={member.surname}>
        </span>
        <span class="data">
                <input type="text" name="email" bind:value={member.email}>
        </span>
        <span class="data">
                <input type="text" name="phoneNumber" bind:value={member.phoneNumber} pattern={phonePattern} required>
        </span>
        <span class="data">
                <LocationSelect all={false} bind:location={member.location} class="locationSelect"></LocationSelect>
        </span>
        <span class="data">
                <input type="number" name="monthlyFee" bind:value={member.monthlyFee} min="0" max="1000">
        </span>
        <span class="data">
            {#each member.categories as category (category.id)}
               <span class="category no-padding">{category.shortname}
                   <button onclick={() => deleteCategoryFromUser(member.uuid, category.id)} type="button">
                   <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path
                           d="M504.6 148.5C515.9 134.9 514.1 114.7 500.5 103.4C486.9 92.1 466.7 93.9 455.4 107.5L320 270L184.6 107.5C173.3 93.9 153.1 92.1 139.5 103.4C125.9 114.7 124.1 134.9 135.4 148.5L278.3 320L135.4 491.5C124.1 505.1 125.9 525.3 139.5 536.6C153.1 547.9 173.3 546.1 184.6 532.5L320 370L455.4 532.5C466.7 546.1 486.9 547.9 500.5 536.6C514.1 525.3 515.9 505.1 504.6 491.5L361.7 320L504.6 148.5z"/></svg>
                </button></span>
            {/each}
            <span class="category no-">
            <select name="category" id="category" bind:value={selectedCategoryId}>
                {#each categories as category}
                    <option value={category.id}>{category.shortname}</option>
                {/each}
            </select>
            <button onclick={() => addCategoryToUser(member.uuid, selectedCategoryId)} type="button">+</button>
            </span>
        </span>
        <span class="data">
            <textarea bind:value={member.comment} name="comment"></textarea>
        </span>
        <span class="data">
            <button type="submit" class="left" aria-label="Zapisz zmiany">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path
                        d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/></svg>
            </button>
        </span>
        <span class="data">
        <button onclick={() => edit = false} class="right" aria-label="Anuluj zmiany">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path
                    d="M504.6 148.5C515.9 134.9 514.1 114.7 500.5 103.4C486.9 92.1 466.7 93.9 455.4 107.5L320 270L184.6 107.5C173.3 93.9 153.1 92.1 139.5 103.4C125.9 114.7 124.1 134.9 135.4 148.5L278.3 320L135.4 491.5C124.1 505.1 125.9 525.3 139.5 536.6C153.1 547.9 173.3 546.1 184.6 532.5L320 370L455.4 532.5C466.7 546.1 486.9 547.9 500.5 536.6C514.1 525.3 515.9 505.1 504.6 491.5L361.7 320L504.6 148.5z"/></svg>
        </button>
            </span>
    </form>
{/if}

<style>

    .category {
        display: inline-block;
        background-color: var(--color-background-secondary);
        font-size: 0.7rem;
        line-height: 15px;
        height: fit-content;
        align-content: center;
        width: fit-content;
        padding: 5px !important;
        border-radius: 10px !important;
        vertical-align: middle;
        margin: 5px;
    }
    .no-padding{
        padding: 0 5px !important;
    }

    .category button {
        height: min-content;
        width: min-content;
    }

    .category button svg,
    .category button path,
    .category button{
        width: 10px;
        height: 100%;
        vertical-align: middle;
    }

    .row {
        margin: 10px 0;
        display: table-row;
        width: 100%;
        /*border: 2px solid var(--color-border);*/
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        z-index: 1;
        transition: outline 0.4s;
    }

    .row:hover {
        outline: 2px solid transparent;
    }

    .row > * {
        line-height: 0;
        vertical-align: middle;
        display: table-cell;
    }

    .data {
        text-align: center;
        height: 100%;
    }

    svg {
        width: 100%;
        display: block;
        align-self: center;
        justify-self: center;
    }

    textarea:focus {
        outline: none !important;
        border: none !important;
    }

    .data:has(textarea) {
        padding: 5px 0;
    }

    textarea {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 10px;
        width: 80%;
        color: var(--color-text-secondary);
    }

    button {
        background-color: var(--color-background-secondary);
        border: none;
        width: 100%;
        height: 100%;
        vertical-align: middle;
        padding: 10px;
        cursor: pointer;
    }

    button.right {
        border-radius: 0 15px 15px 0 !important;
    }

    button.left {
        border-radius: 15px 0 0 15px !important;
        padding: 10px !important;
    }

    svg {
        fill: var(--color-text-secondary);
        height: 25px;
        width: 25px;
        transition: fill 0.6s;
    }

    button:hover svg,
    svg:hover {
        fill: var(--color-text-primary);
    }

    form button {
        align-self: center !important;
    }

    input {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 10px;
        color: var(--color-text-primary);
        text-align: center;
        max-width: 100%;
    }

    .mobile {
        display: none;
    }

    svg {
        width: 100%;
    }

    @media screen and (width <= 1000px) {
        .desktop {
            display: none;
        }

        .mobile {
            display: block;
            outline: 2px solid var(--color-border);
            height: fit-content;
            border-radius: 15px;
            padding: 10px;
            width: calc(100vw - 30px);
            margin: 10px 0;
        }

        .mobile > div span textarea {
            width: min-content;
            max-width: 100%;
            background-color: transparent;
            text-align: right;
            pointer-events: none;
            user-select: none;
            text-decoration: none;
            display: block;
            height: fit-content;
        }

        .mobile > form textarea,
        .mobile > form input,
        .mobile > form :global(.locationSelect) {
            width: 100%;
        }

        .mobile > form div > span + span {
            width: 70%;
        }

        .mobile span {
            min-height: 1em;
            line-height: 1em;
            height: fit-content;
            width: fit-content;
        }

        .mobile button {
            padding: 0;
            margin: 0;
            height: 1em;
            line-height: 1em;
            background-color: transparent;
        }

        .horizontal {
            display: flex;
            justify-content: space-between;
            margin: 5px 0;
            height: fit-content;
        }

        .bold {
            font-weight: 700;
        }

        .horizontal:has(.save) {
            height: fit-content;
        }

        .save {
            background-color: var(--color-background-secondary) !important;
            border-radius: 15px;
            display: block;
            width: min-content;
            padding: 10px !important;
            height: auto !important;
        }
    }
</style>

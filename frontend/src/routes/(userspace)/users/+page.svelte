<script lang="ts">
    import {enhance} from '$app/forms';
    import {onMount} from 'svelte';
    import {locations} from '$lib/stores/locations.svelte';
    import LocationSelect from '$lib/LocationSelect.svelte';
    import Error from '$lib/Error.svelte';

    onMount(() => {
        locations.load(true);
    });

    let {data, form} = $props();

    let users = $derived(data.users);
    let categories = $derived(data.categories);
    // let categories = [];

    const roles = {
        'ROLE_ADMIN': 'Administrator',
        'ROLE_COACH': 'Trener'
    };

    const statuses = {
        'PENDING': 'Oczekujący',
        'ACTIVE': 'Aktywny',
        'DISABLED': 'Wyłączony'
    };

    let inviting = $state(false);

</script>
<svelte:head>
    <title>Baza - Zarządzanie</title>
</svelte:head>

<div class="container gap-4">
    <div class="locations card">
        <h2 class="text-xl m-2">Użytkownicy</h2>
        <Error code={form?.error}></Error>
        <div class="addLocation">
            <form action="?/invite" class="flex flex-col gap-2" method="POST" use:enhance={() => {
            inviting = true;
            return async ({update}) => {
                await update();
                inviting = false;
            }
        }}>
                <input autocomplete="off" class="left" name="email" placeholder="trener@email.com" required
                       type="email"/>
                <select name="role">
                    <option value="ROLE_ADMIN">Administrator</option>
                    <option value="ROLE_COACH">Trener</option>
                </select>
                <button class="right" disabled={inviting} type="submit">{inviting ? "Zapraszanie..." : "Zaproś"}</button>
            </form>
        </div>

        <ul class="mt-2">
            {#each users.sort((a, b) => a.uuid > b.uuid) as user, index (index)}
                <li>
                    <span class="user m-1 bg-(--background-secondary) p-2 rounded-lg shadow-md shadow-slate-950/20">
                        {roles[user.role.name]} {user.email}
                        {data.user.email !== user.uuid ? ", " + statuses[user.status] : ""}
                        {#if data.user.email != user.uuid}
                        <label for="hide{index}"
                               class="bg-(--click)! text-(--text-primary)! rounded-l-md! rounded-r-lg! p-2 shadow-md shadow-slate-950/20">Rozwiń</label>
                            <input type="checkbox" class="hide-checkbox" id="hide{index}">
											<div class="hideable flex gap-2 flex-col">
                            {#if user.status !== "PENDING"}
                                <form action="?/changeStatus" method="POST" use:enhance>
                                    <input type="hidden" name="userUuid" value={user.uuid}>
                                    <input type="hidden" name="status"
                                           value={user.status === "ACTIVE" ? "DISABLED" : "ACTIVE"}>
                                    <button type="submit"
                                            class="deleteAccount">{user.status === "ACTIVE" ? "WYŁĄCZ KONTO" : "WŁĄCZ KONTO"}</button>
                                </form>
                            {/if}
                                                {#if user.role.name !== "ROLE_ADMIN"}
                            {#each user.locations as location (location.id)}
                                <form action="?/deleteLocationFromUser" method="POST" use:enhance
                                      class="flex flex-row justify-center items-center gap-2 align-middle">
                                    <span class="h-fit block">{location.name}</span>
                                    <input type="hidden" name="locationId" value={location.id}>
                                    <input type="hidden" name="userUuid" value={user.uuid}>
                                    <button type="submit">Usuń</button>
                                </form>
                            {/each}
                                                    <form action="?/addLocationToUser" method="POST" use:enhance
                                                          class="flex flex-row justify-center items-center gap-2 h-fit">
                                <input type="hidden" value={user.uuid} name="userUuid">
                                <LocationSelect all={false} class="h-full!"></LocationSelect>
                                <button type="submit" class="h-full!">Dodaj</button>
                            </form>
                        {/if}
                        </div>
                                                {/if}
                    </span>
                </li>
            {/each}
        </ul>
    </div>

    <div class="locations card">
        <h2 class="text-xl m-2">Kategorie</h2>
        {#if form?.error && form?.type == "category"}
            <Error code={form?.error}></Error>
        {/if}
        <div class="1addLocation">
            <form action="?/addCategory" class="gap-2! flex flex-col" method="POST" use:enhance>
                <input class="left" name="name" placeholder="Grupa 1" required type="text">
                <input name="shortname" placeholder="Gr1" required type="text">
                <button class="right w-full" type="submit">Dodaj</button>
            </form>
        </div>

        <ul class="mt-2 flex flex-row! flex-wrap gap-2">
            {#each categories as category, index (index)}
                <li class="bg-(--background-secondary) p-2 rounded-lg w-fit shadow-md shadow-slate-950/20">
                    <form action="?/deleteCategory" method="POST" use:enhance
                    >
                        <span>{category.name} ({category.shortname})</span>
                        <input type="hidden" name="categoryId" value={category.id}>
                        <button type="submit">Usuń</button>
                    </form>
                </li>
            {/each}
        </ul>
    </div>
</div>

<style>
    @import "tailwindcss";

    .card {
        @apply
        text-(--text-primary-dark) bg-(--background-primary) rounded-2xl shadow-md shadow-slate-50/60
        flex! flex-col! justify-baseline p-4
        *:flex! *:flex-col! *:justify-baseline
        h-fit
        ;
    }

    input, select {
        @apply
        bg-(--input)!
        shadow-slate-950/40 shadow-md
        h-10
        ;
    }

    button {
        @apply
        bg-(--click)! text-(--text-primary)!
        shadow-slate-950/40 shadow-md
        h-10
        ;
    }

    input, select {
        width: 100% !important;
    }

    .addLocation button[type='submit'] {
        width: 100% !important;
    }

    .addLocation input,
    .addLocation select,
    .addLocation button {
        padding: 10px !important;
    }

    .deleteAccount {
        padding: 5px 20px;
    }

    button {
        border-radius: 10px;
        padding: 10px;
    }

    .right {
        border-radius: 0 0 15px 15px;
    }

    .leftleft,
    :global(.leftleft) {
        border-radius: 15px 0 0 15px !important;
    }

    .rightright {
        border-radius: 0 15px 15px 0 !important;
    }

    .left,
    :global(.left) {
        border-radius: 15px 15px 0 0 !important;
        padding: 5px !important;
    }

    label {
        user-select: none;
        cursor: pointer;
        border-radius: 10px;
    }

    h2 {
        text-align: center;
    }

    .users,
    .locations {
        display: flex;
        flex-direction: column;
    }

    #addUser,
    .addLocation {
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    #addUser form,
    #addLocation form {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    #addUser form *,
    #addLocation form *,
    .hideable button {
        height: 100% !important;
    }

    .container {
        display: flex;
        flex-direction: row;
        justify-content: space-evenly;
    }

    .container > div {
        width: 50%;
    }

    input,
    button,
    select,
    option,
    label {
        background-color: var(--background-secondary);
        border: none;
        color: var(--text-secondary);
        display: inline-block !important;
        align-self: center;
        text-align: center;
        padding: 5px;
    }

    button,
    select,
    option {
        cursor: pointer !important;
    }

    div.hideable {
        display: flex;
        flex-direction: column;
    }

    .hideable form {
        height: fit-content;
    }

    .locations li,
    .users li {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    .hideable {
        display: flex;
        flex-direction: column
    }

    ul {
        list-style-type: none;
        padding: 0;
    }

    .hide-checkbox {
        display: none !important;
    }

    .user:has(.hide-checkbox:not(:checked)) .hideable {
        display: none;
    }

    @media screen and (width <= 1000px) {
        .container {
            display: flex;
            flex-direction: column;
            width: 100%;
            padding: 0;
            box-sizing: border-box;
        }

        .container > * {
            width: 100% !important;
            box-sizing: border-box;
        }

        /* Invite form */
        #addUser form {
            width: 100%;
            display: flex;
            flex-direction: column !important;
            gap: 4px;
        }

        #addUser form * {
            border-radius: 15px !important;
            width: 100% !important;
            box-sizing: border-box;
            margin: 0 !important;
        }

        /* Add location form */
        #addLocation form {
            width: 100%;
            display: flex;
            flex-direction: column !important;
            margin: 0;
            gap: 4px;
        }

        #addLocation form * {
            border-radius: 15px !important;
            width: 100% !important;
            box-sizing: border-box;
            margin: 0 !important;
        }

        /* Location delete rows in the list */
        .locations li form {
            width: 100%;
            display: flex;
            flex-direction: row;
            align-items: center;
            justify-content: space-between;
            box-sizing: border-box;
        }

        .locations li form span {
            flex: 1;
            text-align: left;
            word-break: break-word;
        }

        /* hideable section (add location to user, location tags) */
        .hideable {
            width: 100%;
        }

        .hideable form {
            width: 100%;
            display: flex;
            flex-direction: row;
            align-items: center;
            flex-wrap: wrap;
            box-sizing: border-box;
        }

        /* addLocationToUser form specifically — select + button inline */
        .hideable form :global(.left) {
            flex: 1;
        }

        .container * {
            margin: 3px;
        }
    }
</style>
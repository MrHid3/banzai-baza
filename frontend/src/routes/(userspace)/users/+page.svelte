<script lang="ts">
    import {enhance} from "$app/forms";
    import {onMount} from "svelte";
    import {locations} from "$lib/stores/locations.svelte";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import Error from "$lib/Error.svelte"

    onMount(() => {
        locations.load(true);
    })

    let {data, form} = $props();

    let users = $derived(data.users);
    let categories = $derived(data.categories);
    // let categories = [];

    const roles = {
        "ROLE_ADMIN": "Administrator",
        "ROLE_COACH": "Trener"
    }

    const statuses = {
        "PENDING": "Oczekujący",
        "ACTIVE": "Aktywny",
        "DISABLED": "Wyłączony"
    }

    let inviting = $state(false);

</script>
<svelte:head>
    <title>Baza - Zarządzanie</title>
</svelte:head>

<div class="container min-w-full">
    <div class="locations">
        <h2>Użytkownicy</h2>
        {#if form?.error && form?.type == "user"}
            <Error code={form?.error}></Error>
        {/if}
        <div class="addLocation">
            <form action="?/invite" method="POST" use:enhance={() => {
            inviting = true;
            return async ({update}) => {
                await update();
                inviting = false;
            }
        }}>
                <input class="left" name="email" placeholder="trener@email.com" required type="email" autocomplete="off" />
                <select name="role">
                    <option value="ROLE_ADMIN">Administrator</option>
                    <option value="ROLE_COACH">Trener</option>
                </select>
                <button class="right" disabled={inviting} type="submit">{inviting ? "Zaprasznie..." : "Zaproś"}</button>
            </form>
        </div>

        <ul>
            {#each users as user, index (index)}
                <li>
                    <span class="user">
                        {roles[user.role.name]} {user.email}, {statuses[user.status]}
                        {#if user.role.name !== "ROLE_ADMIN"}
                            <label for="hide{index}">Rozwiń</label>
                            <input type="checkbox" class="hide-checkbox" id="hide{index}">
                        {/if}
                    </span>
                    <div class="hideable">
                        {#if user.role.name !== "ROLE_ADMIN"}
                            {#if user.status !== "PENDING"}
                                <form action="?/changeStatus" method="POST" use:enhance>
                                    <input type="hidden" name="userUuid" value={user.uuid}>
                                    <input type="hidden" name="status"
                                           value={user.status === "ACTIVE" ? "DISABLED" : "ACTIVE"}>
                                    <button type="submit"
                                            class="deleteAccount">{user.status === "ACTIVE" ? "WYŁĄCZ KONTO" : "WŁĄCZ KONTO"}</button>
                                </form>
                            {/if}
                            {#each user.locations as location (location.id)}
                                <form action="?/deleteLocationFromUser" method="POST" use:enhance>
                                    <span>{location.name}</span>
                                    <input type="hidden" name="locationId" value={location.id}>
                                    <input type="hidden" name="userUuid" value={user.uuid}>
                                    <button type="submit">Usuń</button>
                                </form>
                            {/each}
                            <form action="?/addLocationToUser" method="POST" use:enhance>
                                <input type="hidden" value={user.uuid} name="userUuid">
                                <LocationSelect all={false} class="leftleft"></LocationSelect>
                                <button type="submit" class="rightright">Dodaj</button>
                            </form>
                        {/if}
                    </div>
                </li>
            {/each}
        </ul>
    </div>

    <div class="locations">
        <h2>Lokalizacje</h2>
        {#if form?.error && form?.type == "location"}
            <Error code={form?.error}></Error>
        {/if}
        <div class="addLocation">
            <form action="?/addLocation" method="POST" use:enhance={(update) => {
                return async ({update}) => {
                    await update();
                    await categories.load(true);
        }}}>
                <input class="left" name="name" placeholder="Szkoła Podstawowa nr 720" required type="text">
                <input name="shortname" placeholder="SP720" required type="text">
                <button class="right" type="submit">Dodaj</button>
            </form>
        </div>

        <ul>
            {#each $locations.data as location (location.id)}
                <li>
                    <a class="text-black bg-(--background-secondary) p-1 rounded-lg hover:text- duration-150" href={`/location/${location.id}`}>{location.name}</a>
                </li>
            {/each}
        </ul>
    </div>

    <div class="locations">
        <h2>Kategorie</h2>
        {#if form?.error && form?.type == "category"}
            <Error code={form?.error}></Error>
        {/if}
        <div class="addLocation">
            <form action="?/addCategory" method="POST" use:enhance>
                <input class="left" name="name" placeholder="Grupa 1" required type="text">
                <input name="shortname" placeholder="Gr1" required type="text">
                <button class="right" type="submit">Dodaj</button>
            </form>
        </div>

        <ul>
            {#each categories as category, index (index)}
                <li>
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

    input, select{
        width: 100% !important;
    }

    .addLocation button[type='submit'] {
        width: 100% !important;
    }

    .addLocation input,
    .addLocation select,
    .addLocation button{
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
    :global(.leftleft){
        border-radius: 15px 0 0 15px !important;
    }

    .rightright{
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
    #addLocation form{
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

    .container * {
        margin: 5px;
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

    .user:has(.hide-checkbox:not(:checked)) + .hideable {
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
            margin: 0;
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
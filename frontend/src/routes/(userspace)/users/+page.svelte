<script lang="ts">
    import {enhance} from "$app/forms";
    import {onMount} from "svelte";
    import {locations} from "$lib/stores/locations.svelte";
    import LocationSelect from "$lib/LocationSelect.svelte";

    onMount(() => {
        locations.load(true);
    })

    let {data} = $props();

    let users = $derived(data.users);

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

    console.log(users)

</script>

<div class="container">
    <div class="users">
        <h2>Użytkownicy</h2>
        <div id="addUser">
            <form action="?/invite" method="POST" use:enhance={() => {
            inviting = true;
            return async ({update}) => {
                await update();
                inviting = false;
            }
        }}>
                <input name="email" type="email" class="left">
                <select name="role">
                    <option value="ROLE_ADMIN">Administrator</option>
                    <option value="ROLE_COACH">Trener</option>
                </select>
                <button disabled={inviting} class="right" type="submit">{inviting ? "Zaprasznie..." : "Zaproś"}</button>
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
                                    <button type="submit" class="deleteAccount">{user.status === "ACTIVE" ? "WYŁĄCZ KONTO" : "WŁĄCZ KONTO"}</button>
                                </form>
                            {/if}
                            {#each user.locations as location}
                                <form action="?/deleteLocationFromUser" method="POST" use:enhance>
                                    <span>{location.name}</span>
                                    <input type="hidden" name="locationId" value={location.id}>
                                    <input type="hidden" name="userUuid" value={user.uuid}>
                                    <button type="submit">Usuń</button>
                                </form>
                            {/each}
                            <form action="?/addLocationToUser" method="POST" use:enhance>
                                <input type="hidden" value={user.uuid} name="userUuid">
                                <LocationSelect all={false} class="left"></LocationSelect>
                                <button type="submit" class="right">Dodaj</button>
                            </form>
                        {/if}
                    </div>
                </li>
            {/each}
        </ul>
    </div>

    <div class="locations">
        <h2>Lokalizacje</h2>
        <div id="addLocation">
            <form action="?/addLocation" method="POST" use:enhance={(update) => {
                return async ({update}) => {
                    await update();
                    await locations.load(true);
        }}}>
                <input name="name" type="text" class="left">
                <input name="shortname" type="text">
                <button type="submit" class="right">Dodaj</button>
            </form>
        </div>

        <ul>
            {#each $locations.data as location, index (index)}
                <li>
                    <form action="?/deleteLocation" method="POST" use:enhance={(update) => {
                    return async ({update}) => {
                        await update();
                        await locations.load(true);
                    }
                }}>
                        <span>{location.name} ({location.shortname})</span>
                        <input type="hidden" name="locationId" value={location.id}>
                        <button type="submit">Usuń</button>
                    </form>
                </li>
            {/each}
        </ul>
    </div>
</div>

<style>
    .deleteAccount{
        padding: 5px 20px;
    }

    button {
        border-radius: 10px;
        padding: 10px;
    }

    .right{
        border-radius: 0 15px 15px 0;
    }

    .left,
    :global(.left){
        border-radius: 15px 0 0 15px !important;
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
    #addLocation {
        display: flex;
        justify-content: center;
    }

    #addUser form {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    #addUser form *,
    #addLocation form *,
    .hideable button {
        /*display: block !important;*/
        height: 100% !important;
    }

    .container {
        display: flex;
        flex-direction: row;
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
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
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
    }

    .hide-checkbox {
        display: none !important;
    }

    .user:has(.hide-checkbox:not(:checked)) + .hideable {
        display: none;
    }

    @media screen and (width <= 1000px){
        .container{
            display: flex;
            flex-direction: column;
            width: 100vw;
            padding: 0;
        }

        .container > * {
            width: 100% !important;
        }

        #addUser form {
            width: 100%;
            display: flex;
            flex-direction: column !important;
            margin: 0;
        }

        #addUser form *{
            border-radius: 15px;
        }
    }
</style>
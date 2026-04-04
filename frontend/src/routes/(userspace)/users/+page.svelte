<script lang="ts">
    import {enhance} from "$app/forms";
    import {onMount} from "svelte";
    import {locations} from "$lib/stores/locations.svelte";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import {user} from "$lib/stores/auth";

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

</script>

<div class="user">
    <div id="addUser">
        <form action="?/invite" method="POST" use:enhance={() => {
            inviting = true;
            return async ({update}) => {
                await update();
                inviting = false;
            }
        }}>
            <input name="email" type="email">
            <select name="role">
                <option value="ROLE_ADMIN">Administrator</option>
                <option value="ROLE_COACH">Trener</option>
            </select>
            <button disabled={inviting} type="submit">{inviting ? "Zaprasznie..." : "Zaproś"}</button>
        </form>
    </div>

    <ul>
        {#each users as user, index (index)}
            <li>
                {roles[user.role.name]} {user.email}, {statuses[user.status]}
                <input type="checkbox" class="hide-checkbox">
                <div class="hideable">
                    {#if user.role.name !== "ROLE_ADMIN"}
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
                            <LocationSelect all={false}></LocationSelect>
                            <button type="submit">Dodaj</button>
                        </form>
                    {/if}
                    {#if user.status !== "PENDING"}
                        <form action="?/changeStatus" method="POST" use:enhance>
                            <input type="hidden" name="userUuid" value={user.uuid}>
                            <input type="hidden" name="status" value={user.status === "ACTIVE" ? "DISABLED" : "ACTIVE"}>
                            <button type="submit">{user.status === "ACTIVE" ? "DEZAKTYWUJ" : "AKTYWUJ"}</button>
                        </form>
                    {/if}
                </div>
            </li>
        {/each}
    </ul>
</div>

<div class="location">
    <div id="addLocation">
        <form action="?/addLocation" method="POST" use:enhance={(update) => {
                return async ({update}) => {
                    await update();
                    await locations.load(true);
        }}}>
            <input name="name" type="text">
            <input name="shortname" type="text">
            <button type="submit">Dodaj</button>
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

<style>
    div.hideable {
        display: flex;
        flex-direction: column;
    }

    li form {
        display: flex;
        flex-direction: row;
    }

    .hide-checkbox:not(:checked) + .hideable {
        display: none;
    }
</style>
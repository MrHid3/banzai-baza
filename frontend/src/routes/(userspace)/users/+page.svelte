<script lang="ts">
    import {enhance} from "$app/forms";
    import {onMount} from "svelte";
    import {locations} from "$lib/stores/locations.svelte";

    onMount(() => {
        locations.load();
    })

    let {data} = $props();

    let users = $derived(data.users);

    const roles = {
        "ROLE_ADMIN": "Administrator",
        "ROLE_COACH": "Trener"
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
            <input type="email" name="email">
            <select name="role">
                <option value="ROLE_ADMIN">Administrator</option>
                <option value="ROLE_COACH">Trener</option>
            </select>
            <button type="submit" disabled={inviting}>{inviting ? "Zaprasznie..." : "Zaproś"}</button>
        </form>
    </div>

    <ul>
        {#each users as user, index (index)}
            <li>{roles[user.role.name]} {user.email}, {user.enabled ? "AKTYWNY" : "NIEAKTYWNY"}</li>
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
            <input type="text" name="name">
            <input type="text" name="shortname">
            <button type="submit">Dodaj</button>
        </form>
    </div>

    <ul>
        {#each $locations.data as location, index (index)}
            <li>{location.name} ({location.shortname})</li>
        {/each}
    </ul>
</div>

<style>

</style>
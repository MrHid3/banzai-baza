<script lang="ts">
    import {enhance} from "$app/forms";

    let {data} = $props();

    let users = $state(data.users);

    let locations = $state(data.locations);

</script>

<div class="user">
    <div id="addUser">
        <form action="?/invite" method="POST" use:enhance>
            <input type="email" name="email">
            <select name="role">
                <option value="ROLE_ADMIN">Administrator</option>
                <option value="ROLE_COACH">Trener</option>
            </select>
            <button type="submit">Zaproś</button>
        </form>
    </div>

    <ul>
        {#each users as user, index (index)}
            <li>{user.email}, {user.enabled ? "AKTYWNY" : "NIEAKTYWNY"}</li>
        {/each}
    </ul>
</div>

<div class="location">
    <div id="addLocation">
        <form action="?/addLocation" method="POST" use:enhance>
            <input type="text" name="name">
            <input type="text" name="shortname">
            <button type="submit">Dodaj</button>
        </form>
    </div>

    <ul>
        {#each locations as location, index (index)}
            <li>{location.name} ({location.shortname})</li>
        {/each}
    </ul>
</div>

<style>

</style>
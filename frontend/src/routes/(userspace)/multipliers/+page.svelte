<script lang="ts">
    import {locations} from '$lib/stores/locations.svelte';
    import {onMount} from "svelte";
    import {enhance} from "$app/forms";

    let {data} = $props();

    onMount(() => {
        locations.load(true);
    })

    const currentYear = new Date().getFullYear();

    // Map<locationId, Map<monthIndex (1-12), multiplier>>
    const multiplierMap = $derived(new Map(
        ($locations.data ?? []).map(loc => [
            loc.id,
            new Map(
                (data.multipliers ?? [])
                    .filter(m => m.location.id === loc.id)
                    .map(m => [new Date(m.month).getMonth() + 1, m])
            )
        ])
    ));
</script>

{#snippet multiplierCell(locationId, monthIndex)}
    {@const mult = multiplierMap.get(locationId)?.get(monthIndex)}
    <td>
        <form action="?/addMultiplier" method="POST" use:enhance>
            <input type="hidden" name="locationId" value={locationId}>
            <input type="hidden" name="month" value={monthIndex}>
            <input type="hidden" name="year" value={currentYear}>
            {#if mult}
                <input type="hidden" name="id" value={mult.id}>
            {/if}
            <input type="number" name="multiplier" value={mult?.multiplier ?? 1} step="0.01">
            <button type="submit" class="fa-solid fa-floppy-disk"></button>
        </form>
    </td>
{/snippet}

<table>
    <thead>
    <tr>
        <td>Lokalizacja</td>
        <td>Styczeń</td>
        <td>Luty</td>
        <td>Marzec</td>
        <td>Kwiecień</td>
        <td>Maj</td>
        <td>Czerwiec</td>
        <td>Lipiec</td>
        <td>Sierpień</td>
        <td>Wrzesień</td>
        <td>Październik</td>
        <td>Listopad</td>
        <td>Grudzień</td>
    </tr>
    </thead>
    <tbody>
    {#each $locations.data ?? [] as location (location.id)}
        <tr>
            <td>{location.shortname}</td>
            {#each {length: 12} as _, i}
                {@render multiplierCell(location.id, i + 1)}
            {/each}
        </tr>
    {/each}
    </tbody>
</table>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Noto+Color+Emoji&family=Noto+Emoji:wght@300..700&display=swap');
    @import url("../../../../public/fontawesome-free-7.2.0-web/css/all.min.css");

    * {
        font-weight: normal;
        font-family: 'Ubuntu', sans-serif, "Noto Color Emoji", sans-serif;
        font-optical-sizing: auto;
    }

    table {
        width: 100%;
        table-layout: fixed;
        border-collapse: collapse;
        border-radius: 15px !important;
        overflow: hidden;
    }

    thead {
        background-color: var(--color-background-secondary);
        height: 45px;
    }

    thead td {
        font-weight: bold;
        padding: 10px;
    }

    tbody tr:nth-child(even) {
        background-color: var(--color-background-secondary);
    }

    td {
        text-align: center;
        padding: 8px 5px;
    }

    td:first-child {
        width: 120px;
        text-align: left;
        padding-left: 15px;
    }

    td form {
        display: flex;
        flex-direction: row;
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        overflow: hidden;
    }

    td form > * {
        flex: 1;
        height: 40px;
        text-align: center;
        border: none;
        background-color: var(--color-background-secondary);
        color: var(--color-text-primary);
    }

    td form input[type="number"] {
        width: 100%;
        flex: 2;
        border: none;
        color: var(--color-text-secondary);
        text-align: center;
        padding: 0 0.5em;
        background-color: var(--color-background-primary);
    }

    td form input[type="number"]:focus {
        border: none;
        outline: none;
    }

    td form button {
        border-radius: 0 15px 15px 0;
        cursor: pointer;
        background-color: var(--color-background-secondary);
        color: var(--color-text-primary);
    }
</style>
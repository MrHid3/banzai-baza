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
            <button type="submit">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30">
                    <path d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/>
                </svg>
            </button>
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
        background-color: var(--color-background-primary);
        padding: 5px;
        color: var(--color-text-primary);
    }

    svg{
        fill: var(--color-text-secondary)
    }

    button:hover svg,
    svg:hover{
        fill: var(--color-text-primary)
    }
</style>
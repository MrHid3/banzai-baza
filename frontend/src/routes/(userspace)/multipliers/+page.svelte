<script lang="ts">
    import {locations} from '$lib/stores/locations.svelte';
    import {onMount} from "svelte";
    import {enhance} from "$app/forms";

    let {data} = $props();

    onMount(() => {
        locations.load(true);
    })

    const currentYear = new Date().getFullYear();

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

    const monthNames = [
        'Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec',
        'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'
    ];

    // Track which location cards are expanded
    let openLocations = $state<Set<number>>(new Set());

    function toggle(id: number) {
        const next = new Set(openLocations);
        if (next.has(id)) {
            next.delete(id);
        } else {
            next.add(id);
        }
        openLocations = next;
    }
</script>
<svelte:head>
    <title>Baza - Mnożniki</title>
</svelte:head>

{#snippet multiplierCell(locationId: number, monthIndex: number)}
    {@const mult = multiplierMap.get(locationId)?.get(monthIndex)}
    <form action="?/addMultiplier" method="POST" use:enhance class="cell-form">
        <input type="hidden" name="locationId" value={locationId}>
        <input type="hidden" name="month" value={monthIndex}>
        <input type="hidden" name="year" value={currentYear}>
        {#if mult}
            <input type="hidden" name="id" value={mult.id}>
        {/if}
        <input type="number" name="multiplier" value={mult?.multiplier ?? 1} step="0.01">
        <button type="submit" aria-label="Zapisz">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="18" width="18">
                <path d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/>
            </svg>
        </button>
    </form>
{/snippet}

<div class="cards-container">
    {#each $locations.data ?? [] as location (location.id)}
        {@const isOpen = openLocations.has(location.id)}
        <div class="card">
            <button
                    class="card-header"
                    onclick={() => toggle(location.id)}
                    aria-expanded={isOpen}
            >
                <span class="location-name">{location.shortname}</span>
                <svg
                        class="chevron"
                        class:rotated={isOpen}
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 24 24"
                        width="16"
                        height="16"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                >
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>

            {#if isOpen}
                <div class="month-grid">
                    {#each {length: 12} as _, i}
                        <div class="month-cell">
                            <span class="month-label">{monthNames[i]}</span>
                            {@render multiplierCell(location.id, i + 1)}
                        </div>
                    {/each}
                </div>
            {/if}
        </div>
    {/each}
</div>

<style>
    @import 'tailwindcss';

    .cards-container {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .card {
        border: 0.5px solid var(--color-border);
        border-radius: 12px;
        overflow: hidden;
        background-color: var(--color-background-primary);
    }

    .card-header {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.875rem 1rem;
        background-color: var(--color-background-secondary);
        border: none;
        cursor: pointer;
        color: var(--color-text-primary);
    }

    .card-header:hover {
        background-color: var(--color-background-tertiary);
    }

    .location-name {
        font-weight: bold;
        font-size: 0.95rem;
        font-family: 'Ubuntu', sans-serif;
    }

    .chevron {
        color: var(--color-text-secondary);
        transition: transform 0.2s ease;
        flex-shrink: 0;
    }

    .chevron.rotated {
        transform: rotate(180deg);
    }

    .month-grid {
        display: flex;
        flex-wrap: wrap;
        border-collapse: collapse;
        grid-template-rows: 1fr 1fr 1fr 1fr;
        grid-template-columns: repeat(3, 1fr);
        border-top: 0.5px solid var(--color-border);
        width: 100%;
    }

    .month-cell {
        display: flex;
        flex-direction: column;
        align-items: stretch;
        padding: 0.625rem 0.5rem;
        border-right: 0.5px solid var(--color-border);
        border-bottom: 0.5px solid var(--color-border);
        gap: 0.375rem;
        flex: 1;
        max-width: 33%;
    }

    /* Remove right border on every 4th cell */
    /*.month-cell:nth-child(4n) {*/
    /*    border-right: none;*/
    /*}*/

    /* Remove bottom border on last row (cells 9-12) */
    /*.month-cell:nth-child(n+9) {*/
    /*    border-bottom: none;*/
    /*}*/

    .month-label {
        font-size: 0.7rem;
        color: var(--color-text-secondary);
        text-align: center;
        font-family: 'Ubuntu', sans-serif;
    }

    .cell-form {
        display: flex;
        flex-direction: row;
        outline: 2px solid var(--color-border);
        border-radius: 10px;
        overflow: hidden;
    }

    .cell-form > * {
        height: 36px;
        border: none;
        background-color: var(--color-background-secondary);
        color: var(--color-text-primary);
    }

    .cell-form input[type="number"] {
        flex: 2;
        min-width: 0;
        text-align: center;
        padding: 0 0.25em;
        background-color: var(--color-background-primary);
        color: var(--color-text-secondary);
        font-family: 'Ubuntu', sans-serif;
    }

    .cell-form input[type="number"]:focus {
        outline: none;
    }

    .cell-form button {
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0 6px;
        border-radius: 0 10px 10px 0;
        cursor: pointer;
        background-color: var(--color-background-primary);
        color: var(--color-text-primary);
    }

    .cell-form button svg {
        fill: var(--color-text-secondary);
    }

    .cell-form button:hover svg {
        fill: var(--color-text-primary);
    }
</style>
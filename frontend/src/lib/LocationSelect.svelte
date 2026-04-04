<script lang="ts">
    import {locations} from "$lib/stores/locations.svelte"
    import {onMount} from "svelte";

    let {
        location = $bindable(),
        short = true,
        all = false,
        class: componentClass = ""
    }
        = $props();
    onMount(() => locations.load());

    $effect(() => {
        if (selectedLocationId != -1) {
            location = $locations.data.find(e => e.id === selectedLocationId)
        } else {
            location = null;
        }
    })

    let selectedLocationId = $state(-1);

    $effect(() => {
        if (!all && location != null) {
            selectedLocationId = location.id;
        }else if(!all && $locations.data[0] != undefined) {
            selectedLocationId = $locations.data[0].id;
        }
    })
</script>


<select bind:value={selectedLocationId} class={componentClass} id="locationSelect" name="locationId">
    {#if all}
        <option value={-1}>Wszystkie</option>
    {/if}
    {#each $locations.data as location, index(index)}
        <option value={location.id}>{short ? location.shortname : location.name}</option>
    {/each}
</select>

<style>
    select, option {
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
        display: inline-block !important;
        align-self: center;
        text-align: center;
        /*font-size: 0.8em;*/
        /*line-height: 2em;*/
        padding: 0;
        width: fit-content;
    }

    select {
        /*padding: 10px;*/
        height: 100%;
        display: block;
    }
</style>
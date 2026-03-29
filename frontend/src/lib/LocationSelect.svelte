<script lang="ts">
    import {locations} from "$lib/stores/locations.svelte"
    import {onMount} from "svelte";

    let { value = $bindable(), short = true } = $props();
    onMount(() => locations.load());
</script>


<select bind:value={value} id="locationSelect" name="locations">
    <option selected value={-1}>Wszystkie</option>
    {#each $locations.data as location, index(index)}
        <option value={location.id}>{short? location.name : location.shortName}</option>
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
    }
</style>
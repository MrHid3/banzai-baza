<script lang="ts">
    import { onMount } from 'svelte'
    import { goto } from '$app/navigation'
    import { isAuthenticated } from "../stores/auth.ts";
    import { refreshAccessToken} from "$lib/fetchWithAuth.js";

    let checked = false;

    onMount(() => {

        if(!$isAuthenticated) {
            refreshAccessToken(fetch);
            if(!$isAuthenticated) {
                goto('/login');
            }
        }
        checked = true;
    })
</script>

{#if checked && isAuthenticated}
    <slot/>
{/if}
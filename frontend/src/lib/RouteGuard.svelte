<script lang="ts">
    import { onMount } from 'svelte'
    import { goto } from '$app/navigation'
    import {isAuthenticated, token} from "../stores/auth.ts";
    import { refreshAccessToken} from "$lib/fetchWithAuth.js";
    import {browser} from "$app/environment";

    let checked = false;

    onMount(async () => {

        if(!$isAuthenticated) {
            await refreshAccessToken(fetch);
        }
        if(!$isAuthenticated) {
            goto('/login');
        }
        checked = true;
    })

</script>

{#if !checked}
    <div></div>
{:else }
    <slot/>
{/if}
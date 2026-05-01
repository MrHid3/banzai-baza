<script lang="ts">
    import {enhance} from "$app/forms";
    import {browser} from "$app/environment";
    import Error from "$lib/Error.svelte"

    let {form} = $props();

    const email = browser ? new URLSearchParams(window.location.search).get("email") : null;

</script>

{#if !form?.ok}
    <form action="?/request" method="POST" use:enhance>
        Email:
        <input name="email" type="email" value={email} required>
        <button type="submit">Resetuj hasło</button>
    </form>
    {#if form?.error}
        <Error code={form?.error}></Error>
    {/if}
{:else}
    <p>Email został wysłany</p>
    <a href="/login">Powrót do loginu</a>
{/if}

<style>
    form,
    p,
    a {
        display: flex;
        flex-direction: column;
        width: fit-content;
        margin: 20px auto;
    }

    form{
        gap: 5px;
    }

    a{
        color: var(--color-text-secondary);
        text-decoration: underline;
    }

    input,
    button {
        border: none;
        border-radius: 10px;
        padding: 5px;
        background-color: var(--color-background-secondary);
        color: var(--color-text-primary);
    }

    button{
        cursor: pointer;
    }

</style>
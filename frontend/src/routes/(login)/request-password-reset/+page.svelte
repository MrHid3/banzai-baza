<script lang="ts">
    import {enhance} from "$app/forms";
    import {browser} from "$app/environment";
    import Error from "$lib/Error.svelte"

    let {form} = $props();

    const email = browser ? new URLSearchParams(window.location.search).get("email") : null;

</script>

<main class="w-screen h-screen flex flex-column justify-center items-start p-50 *:text-(--text-primary)">
    {#if !form?.ok}
        <form action="?/request" method="POST" use:enhance class="bg-(--background-primary) p-4 rounded-2xl text-(--text-primary-dark) shadow-slate-50/40 shadow-md">
            <input name="email" type="email" value={email} required placeholder="Email" class="bg-(--input) p-2 rounded-md shadow-slate-95040 shadow-md">
            <button type="submit">Resetuj hasło</button>
        </form>
        {#if form?.error}
            <Error code={form?.error}></Error>
        {/if}
    {:else}
        <p>Email został wysłany</p>
        <a href="/login">Powrót do loginu</a>
    {/if}
</main>

<style>
    @import "tailwindcss";

    form,
    p,
    a {
        display: flex;
        flex-direction: column;
        width: fit-content;
    }

    form {
        gap: 5px;
    }

    a {
        color: var(--text-secondary);
        text-decoration: underline;
    }

    button {
        cursor: pointer;
        @apply
        bg-(--click) text-(--text-primary) rounded-md shadow-md shadow-slate-950/40
        hover:-translate-y-1! duration-100 p-2
        ;
    }

</style>
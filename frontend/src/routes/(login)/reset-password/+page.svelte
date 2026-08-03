<script lang="ts">
    import {browser} from "$app/environment";

    import {enhance} from "$app/forms";

    let {data, form} = $props();

    let checked = $state(false);

    const token = browser ? new URLSearchParams(window.location.search).get("token") : null;

    $effect(() => {
        if (data.ok) {
            checked = true;
        } else {
            checked = true;
        }
    })

    let password = $state("");
    let showPassword = $state(false);

</script>
<svelte:head>
    <title>Ustaw hasło</title>
</svelte:head>
<main class="w-screen h-screen pt-30">

    {#if !form?.ok}
        {#if !checked}
            Ładowanie...
            <!--    TODO: animacja ładowania-->
        {:else if !data.ok}
            Link wygasł
        {:else}
            <form action="?/confirm" method="POST" use:enhance
                  class="bg-(--background-primary) p-4 rounded-2xl shadow-slate-50/60 shadow-md text-(--text-primary-dark)!">
                <input type="hidden" value={token} name="token">
                Nowe hasło: <input type={showPassword? "text" : "password"} name="password" bind:value={password}
                                   required/>
                Powtórz hasło: <input type={showPassword? "text" : "password"} pattern={password} required/>
                <label for="show">
                    <input type="checkbox" bind:checked={showPassword}>
                    Pokaż hasło
                </label>
                <button type="submit">Zapisz</button>
                {#if form?.error}
                    <span class="error">{form?.error}</span>
                {/if}
            </form>
        {/if}
    {:else}
        <p>Hasło zostało zresetowane</p>
        <a href="/login">Powrót do loginu</a>
    {/if}
</main>
<style>
    @import "tailwindcss";

    form {
        display: flex;
        flex-direction: column;
        width: 500px;
        margin: 0 auto;
    }

    span.error {
        color: red;
        max-width: 100%;
        display: block;
    }

    p, a {
        margin: 0 auto;
        text-align: center;
        display: block;
    }

    a {
        text-decoration: underline;
    }

    input {
        @apply
        bg-(--input)
        p-2 text-(--text-primary)
            rounded-md shadow-md shadow-slate-950/40
        ;
    }

    button {

        cursor: pointer;
        @apply
        bg-(--click) text-(--text-primary) rounded-md shadow-md shadow-slate-950/40
        hover:-translate-y-1! duration-100
            p-2
        ;
    }
</style>
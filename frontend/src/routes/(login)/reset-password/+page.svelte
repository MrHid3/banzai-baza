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
{#if !form?.ok}
    {#if !checked}
        Ładowanie...
        <!--    TODO: animacja ładowania-->
    {:else if !data.ok}
        Link wygasł
    {:else}
        <form action="?/confirm" method="POST" use:enhance>
            <input type="hidden" value={token} name="token">
            Noew hasło: <input type={showPassword? "text" : "password"} name="password" bind:value={password} required/>
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
<style>
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

    p, a{
        margin: 0 auto;
        text-align: center;
        display: block;
    }

    a{
        color: var(--color-text-secondary);
        text-decoration: underline;
    }
</style>
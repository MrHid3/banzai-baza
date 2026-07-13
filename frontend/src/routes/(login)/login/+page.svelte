<script lang="ts">
    import {enhance} from "$app/forms"
    import {goto} from "$app/navigation";
    import Error from "$lib/Error.svelte"
    import {PUBLIC_BACKEND_2} from "$env/static/public";

    let {form} = $props()

    const passwordPattern = ".{8,}";

    let showPassword = $state(false);
    let email = $state("");

    $effect(() => {
        if (form?.token) {
            goto('/db');
        }
    })

    //TODO: Fancy loading type shit
</script>
<svelte:head>
    <title>Login</title>
</svelte:head>

<form action="?/login" method="POST" use:enhance>
    <input name="email" placeholder="Email" type="email" bind:value={email} required>
    <input name="password" placeholder="Hasło" type={showPassword? "text" : "password"} required>
    <label for="show">
        <input bind:checked={showPassword} name="show" type="checkbox">
        Pokaż hasło
    </label>
    <button id="send" type="submit">Zaloguj się</button>
    <a href={"/request-password-reset?email=" + encodeURIComponent(email)}>Zapomniałem/-am hasła</a>

    {#if form?.error}
        <Error code={form?.error}></Error>
    {/if}
</form>

<style>
    * {
        background-color: var(--background-primary);
        color: var(--text-primary);
    }

    form {
        position: absolute;
        top: 30%;
        left: 50%;
        transform: translate(-50%, 50%);
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    input:not([type=checkbox]), #send {
        width: 15vw;
        min-width: 200px;
        padding: 10px;
        border: 1px solid var(--border)
    }

    input:invalid:not(:focus):not(:placeholder-shown) {
        border: 1px solid red;
    }

    form:has(input:invalid:not(:focus)) #send {
        opacity: 0.7;
    }

    button {
        cursor: pointer;
    }

</style>
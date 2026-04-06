<script lang="ts">
    import {enhance} from "$app/forms";
    import LocationSelect from "$lib/LocationSelect.svelte";
    import type {ActionData} from "../../../../.svelte-kit/types/src/routes/$types";
    import {onMount} from "svelte";

    // eslint-disable-next-line svelte/no-unused-props
    let {member = $bindable()}: {
        member: {
            uuid: string,
            name: string,
            surname: string,
            email: string,
            phoneNumber: string,
            monthlyFee: number,
            location: {
                name: string,
                shortname: string,
                id: number
            },
            comment: string
            // payments:
            //     {
            //         month: number,
            //         year: number,
            //         method: string,
            //         amount: number,
            //         active: boolean
            //     }[]
        }
    } = $props();

    let edit = $state(false);

    const phonePattern = "/^[\+]?[0-9]{0,3}\W?+[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im";

</script>

{#if !edit}
    <div class="row">
        <span class="data">{member.name}</span>
        <span class="data">{member.surname}</span>
        <span class="data">{member.email}</span>
        <span class="data">{member.phoneNumber}</span>
        <span class="data">{member.location? member.location.shortname : ""}</span>
        <span class="data">{member.monthlyFee}</span>
        <span class="data">
            <div class="textarea">
                <textarea readonly>{member.comment}</textarea>
            </div>
        </span>
        <span class="data">
            <button onclick={() => edit = true}>
                <svg class="bi bi-pencil-square" fill="currentColor" height="30" viewBox="0 0 16 16" width="30"
                     xmlns="http://www.w3.org/2000/svg">
                    <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                    <path d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"
                          fill-rule="evenodd"/>
                </svg>
            </button>
        </span>
        <span class="data">
            <form action="?/delete" method="POST" use:enhance class="hidden">
                <input type="hidden" name="uuid" value={member.uuid}>
                <button type="submit" title="delete">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-trash3-fill"
                         viewBox="0 0 16 16">
                        <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                    </svg>
                </button>
            </form>
        </span>
    </div>
{:else}
    <form action="?/update" method="POST" class="row" use:enhance={() => {
           return({ result }) => {
               if (result.type === "success") {
                   edit = false;
           }
    }}}>
        <input type="hidden" name="uuid" bind:value={member.uuid}>
        <span class="data">
                <input type="text" name="name" bind:value={member.name}>
        </span>
        <span class="data">
                <input type="text" name="surname" bind:value={member.surname}>
        </span>
        <span class="data">
                <input type="text" name="email" bind:value={member.email}>
        </span>
        <span class="data">
                <input type="text" name="phoneNumber" bind:value={member.phoneNumber} pattern={phonePattern}>
        </span>
        <span class="data">
                <LocationSelect all={false} bind:location={member.location} class="locationSelect"></LocationSelect>
        </span>
        <span class="data">
                <input type="number" name="monthlyFee" bind:value={member.monthlyFee}>
        </span>
        <span class="data">
            <textarea bind:value={member.comment} name="comment"></textarea>
        </span>
        <span class="data">
            <button type="submit">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" height="30" width="30"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path
                        d="M160 96C124.7 96 96 124.7 96 160L96 480C96 515.3 124.7 544 160 544L480 544C515.3 544 544 515.3 544 480L544 237.3C544 220.3 537.3 204 525.3 192L448 114.7C436 102.7 419.7 96 402.7 96L160 96zM192 192C192 174.3 206.3 160 224 160L384 160C401.7 160 416 174.3 416 192L416 256C416 273.7 401.7 288 384 288L224 288C206.3 288 192 273.7 192 256L192 192zM320 352C355.3 352 384 380.7 384 416C384 451.3 355.3 480 320 480C284.7 480 256 451.3 256 416C256 380.7 284.7 352 320 352z"/></svg>
            </button>
        </span>
        <span class="data">
        <button onclick={() => edit = false}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" width="30" height="30"><!--!Font Awesome Free v7.2.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.--><path
                    d="M504.6 148.5C515.9 134.9 514.1 114.7 500.5 103.4C486.9 92.1 466.7 93.9 455.4 107.5L320 270L184.6 107.5C173.3 93.9 153.1 92.1 139.5 103.4C125.9 114.7 124.1 134.9 135.4 148.5L278.3 320L135.4 491.5C124.1 505.1 125.9 525.3 139.5 536.6C153.1 547.9 173.3 546.1 184.6 532.5L320 370L455.4 532.5C466.7 546.1 486.9 547.9 500.5 536.6C514.1 525.3 515.9 505.1 504.6 491.5L361.7 320L504.6 148.5z"/></svg>
        </button>
            </span>
    </form>
{/if}

<style>
    div.textarea {
        width: 100%;
        height: 100%;
        overflow: hidden;
        display: flex;
        align-items: center;
        font-size: 0.8em;
    }

    :global(.locationSelect),
    form input {
        padding: 10px !important;
        font-size: 1.5rem !important;
    }

    .data button {
        border-radius: 15px;
        background-color: var(--color-background-secondary);
        display: flex;
        align-items: center;
        justify-content: center;
        border: none;
        width: 50px;
        height: 50px;
        margin: 0 auto
    }

    form.hidden, form.hidden input {
        border: none;
        background: none;
        padding: 0;
        margin: 0;
        box-sizing: border-box;
        height: 100%;
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    * {
        font-size: 1.5rem;
        color: #E0E0E0;
        padding: 0;
        box-sizing: border-box;
    }

    .row {
        display: table-row !important;
        height: fit-content;
    }

    .data {
        display: table-cell;
        padding: 10px;
        height: fit-content;
        max-width: 20%;
        min-width: 3em;
        width: fit-content;
        vertical-align: middle;
        text-wrap: nowrap;
        align-content: center;
        align-items: center;
    }

    textarea {
        width: 100%;
        background-color: rgba(255, 255, 255, 0.1);
        border: none;
        font-size: 0.6em;
        padding: 10px;
        field-sizing: content;
        resize: none;
        text-wrap: nowrap;
        vertical-align: middle;
    }

    span:has(svg), button:has(svg) {
        cursor: pointer;
    }

    svg {
        fill: var(--color-text-primary);
    }

    input {
        background-color: var(--color-background-secondary);
        border: none;
        color: var(--color-text-secondary);
        display: inline-block !important;
        align-self: center;
        text-align: center;
        width: 100%;
    }

</style>

<!--    Background: #121212 (charcoal black)
    Primary Text: #E0E0E0 (light gray)
    Secondary Text: #B0B0B0 (medium gray)
    Borders/Dividers: #444444 (dark gray)
    Accent: #888888 (soft gray)-->
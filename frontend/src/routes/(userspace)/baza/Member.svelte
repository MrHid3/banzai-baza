<script lang="ts">

    // eslint-disable-next-line svelte/no-unused-props
    let { member, componentClass } : {
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
            },
            componentClass: string,
    } = $props();

</script>

<div class="row">
    <span class="data">{member.name}</span>
    <span class="data">{member.surname}</span>
    <span class="data">{member.email}</span>
    <span class="data">{member.phoneNumber}</span>
    <span class="data">{member.location.shortname}</span>
    <span class="data">{member.monthlyFee}</span>
    <span class="data"><textarea>{member.comment}</textarea></span>
    <span class="data">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16">
    <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
    <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
        </svg>
    </span>
    <span class="data">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">
    <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
    </svg>
    </span>
</div>

<style>
    *{
        font-size: 1.2em;
        color: #E0E0E0;
        padding: 0;
        box-sizing: border-box;
    }

    .row{
        display: table-row;
    }

    .data {
        display: table-cell;
        padding: 10px;
        height: 25px;
        width: 12.5%;
        vertical-align: middle;
        text-wrap: nowrap;
    }

    textarea{
        width: min-content;
        /*resize: none;*/
        background-color: rgba(255, 255, 255, 0.1);
        border: none;
        font-size: 0.6em;
        padding: 10px;
        field-sizing: content;
        resize: none;
        text-wrap: nowrap;
    }

</style>

<!--    Background: #121212 (charcoal black)
    Primary Text: #E0E0E0 (light gray)
    Secondary Text: #B0B0B0 (medium gray)
    Borders/Dividers: #444444 (dark gray)
    Accent: #888888 (soft gray)-->
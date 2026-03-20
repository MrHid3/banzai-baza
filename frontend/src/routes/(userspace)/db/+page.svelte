<script lang="ts">
    import type {PageProps} from "../../../../.svelte-kit/types/src/routes"
    import Member from "./Member.svelte";
    // import {PUBLIC_BACKEND_URL} from "$env/static/public";

    let { data }: PageProps = $props();

    const members = [{
        id: 1,
        name: "Adam",
        surname: "Małysz",
        email: "amalysz@gmail.com",
        phoneNumber: "123456789",
        location: "SP 24",
        monthlyFee: 150,
        payments: [
            {
                date: "2026-02-02",
                amount: 150,
                method: "cash",
                active: true,
            },
            {
                date: "2026-01-01",
                amount: 150,
                method: "debit",
                active: true,
            }
        ]
    }]


</script>

<div class="container">
    <div class="membersTable" style="--number-of-elements-minus-four: {members.length + 1 - 4}">
        <div class="header">
            <span class="data">Imię</span>
            <span class="data">Nazwisko</span>
            <span class="data">Email</span>
            <span class="data">Numer telefonu</span>
            <span class="data">{data.json}</span>
        </div>
        {#each members as member}
            <Member componentClass="Member" member={member}></Member>
        {/each}
    </div>
</div>

<style>
    .membersTable > * {
        border-color: #444444;
        border-style: solid;
        border-width: 0 0 2px 0;
    }

    .membersTable{
        display: table;
        text-align: center;
        line-height: 2.4em;
        font-size: 1.2em;
        margin: 0 auto;
    }

    .data{
        padding: 5px;
        display: table-cell;
    }

    .header{
        display: table-row;
    }


</style>

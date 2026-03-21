<script lang="ts">
    import Member from "./Member.svelte";

    let { data } = $props();

    let members = $state(data.members);
</script>

<div class="container">
    <div class="membersTable" style="--number-of-elements-minus-four: {members.length + 1 - 4}">
        <div class="header">
            <span class="data">Imię</span>
            <span class="data">Nazwisko</span>
            <span class="data">Email</span>
            <span class="data">Numer telefonu</span>
<!--            <span class="data">{data.json}</span>-->
        </div>
        {#each members as member, index (index)}
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

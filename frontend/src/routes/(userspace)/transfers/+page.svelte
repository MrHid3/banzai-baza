<script lang="ts">
    import {untrack} from "svelte";

    let {data} = $props();

    class Transfer {
        uuid: string;
        amount: number;
        title: string;
        bookingDate: string;
        validTitle: string;
        hidden: boolean;
    }

    let transfers: Transfer[] = $derived(data.transfers.sort((a, b) => new Date(b.bookingDate) - new Date(a.bookingDate)));

    let months = $state([]);
    let showHidden = $state(false);

    const dateToMonthYear = (date: string) => {
        const dateDate = new Date(date);
        return `${dateDate.getMonth()}-${dateDate.getFullYear()}`
    }

    $effect(() => {
        transfers;
        untrack(() => {
            months = [];
            for (let transfer of transfers) {

                const data = dateToMonthYear(transfer.bookingDate);
                if (!months.some(a => a.month == data))
                    months.push({month: data, transfers: [], isOpen: false});
                months[months.findIndex(a => a.month == data)].transfers.push(transfer);
            }
            months[0].isOpen = true;
        })
    })
    $inspect(months);

</script>

<div class="bg-(--background-primary) p-4 rounded-2xl shadow-md shadow-slate-50/60 w-2/3 mx-auto mb-4">
    <button class="button" onclick={() => {
        months.reverse()
        for(let i = 0; i < months.length; i++){
            months[i].transfers.reverse();
        }
    }}>
        {months.length > 1 ? months[0].month < months[1].month ? "Sortuj od najnowszego" : "Sortuj od najstarszego" : ""}
    </button>
    <button class="button" onclick={() => {
        showHidden = !showHidden;
    }}>
        {showHidden ? 'Schowaj ukryte' : 'Pokaż ukryte'}
    </button>
</div>

<main class="bg-(--background-primary) w-2/3 mx-auto h-fit min-h-1/3 flex flex-col text-(--text-primary-dark) p-4 rounded-2xl shadow-md shadow-slate-50/60 gap-2">
    {#each months as month (month.month)}
        <div class="bg-(--background-secondary) w-full p-2 shadow-md shadow-slate-950/40 rounded-md has-[summary:hover]:bg-(--hover) duration-200">
            <details bind:open={month.isOpen} class="">
                <summary class="list-none flex flex-row justify-between cursor-pointer duration-200 p-2 rounded-md">
                    <span class="capitalize text-2xl">{new Date(`${month.month}-09`).toLocaleString('pl', {
                        month: "long",
                        year: "numeric"
                    })}</span>
                    <div class="flex flex-row items-center gap-4">
                        <svg
                                class="chevron duration-75"
                                xmlns="http://www.w3.org/2000/svg"
                                class:rotate-180={month.isOpen}
                                viewBox="0 0 24 24"
                                width="16"
                                height="16"
                                fill="none"
                                stroke="black"
                                stroke-width="2"
                        >
                            <polyline points="6 9 12 15 18 9"/>
                        </svg>
                    </div>
                </summary>
                <div class="grid grid-cols-1 xl:grid-cols-2 gap-4">
                    {#each month.transfers as transfer (transfer.uuid)}
                        {#if !transfer.hidden || showHidden}
                            <div class="shadow-md shadow-slate-950/40 rounded-md p-2 flex flex-col gap-2 duration-200"
                                 class:bg-red-300={!transfer.validTitle}
                                 class:bg-green-300={transfer.validTitle}>
                                <div class="flex flex-row w-full justify-between text-xl">
                                    <span>{transfer.title}</span>
                                    <span>{transfer.amount} zł</span>
                                </div>
                                <div class="flex flex-row w-full justify-between items-end h-10">
                                    <span class="text-(--text-secondary)">{transfer.bookingDate}</span>
                                    <span class="flex flex-row gap-2">
                                    <button onclick={transfer.hidden = !transfer.hidden}>
                                        {transfer.hidden? "Przestań ukrywać" : "Ukryj"}
                                    </button>
                                    <label for={`isValid-${transfer.uuid}`} class="switch">
                                        <input id={`isValid-${transfer.uuid}`} type="checkbox" class="hidden"
                                               bind:checked={transfer.validTitle}>
                                    </label>
                                </span>
                                </div>
                            </div>
                        {/if}
                    {/each}
                </div>
            </details>
        </div>
    {/each}
</main>

<style>
    @import "tailwindcss";

    button {
        @apply
        bg-(--click) text-(--text-primary) p-2 rounded-md cursor-pointer duration-200 shadow-md shadow-slate-950/40
        hover:bg-(--hover)
        ;
    }

    .switch {
        @apply
        w-14 h-10 bg-(--click) block rounded-md relative shadow-md shadow-slate-950/40 cursor-pointer
        before:w-6 before:h-8 before:bg-(--input) before:absolute before:rounded-md before:top-1 before:left-1 before:shadow-xs before:shadow-slate-50/20
        has-checked:before:left-7 before:duration-200
        ;
    }
</style>
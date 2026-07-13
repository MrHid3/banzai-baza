<script lang="ts">
    import { enhance } from '$app/forms';
    import LocationSelect from '$lib/LocationSelect.svelte';
    import Error from "$lib/Error.svelte";

    let { data, form } = $props();

    let members = $state(data.members ?? []);
    let categories = $derived(data.categories);

    $effect(() => {
        members = data.members?.sort((a, b) => a.uuid.localeCompare(b.uuid)) ?? [];
    });

    // Filter state
    let selectedLocation = $state(null);
    let selectedMembers = $state<string[]>([]);
    let selectedCategories = $state<number[]>([]);
    let filterMode = $state<'OR' | 'AND'>('OR');
    let memberSearchText = $state('');

    // SMS composition state
    let messageText = $state('');
    let scheduleDate = $state('');
    let scheduleTime = $state('');
    let scheduleType = $state<'now' | 'scheduled'>('now');

    // Filtered members computed value
    let filteredMembers = $derived.by(() => {
        let result = members;

        // Location filter
        if (selectedLocation != null) {
            result = result.filter((m) => m.location.id === selectedLocation.id);
        }

        // Member text search
        const search = memberSearchText.trim();
        if (search.length >= 2) {
            result = result.filter((m) =>
                m.name?.toLowerCase().includes(search.toLowerCase()) ||
                m.surname?.toLowerCase().includes(search.toLowerCase()) ||
                m.email?.toLowerCase().includes(search.toLowerCase()) ||
                m.phoneNumber?.toLowerCase().includes(search.toLowerCase())
            );
        }

        // Category filter with AND/OR logic
        if (selectedCategories.length > 0) {
            if (filterMode === 'OR') {
                result = result.filter((m) =>
                    m.categories.some((c) => selectedCategories.includes(c.id))
                );
            } else {
                result = result.filter((m) =>
                    selectedCategories.every((catId) =>
                        m.categories.some((c) => c.id === catId)
                    )
                );
            }
        }

        return result;
    });

    let messageCharCount = $derived(messageText.length);
    let maxMessageLength = 160;

    function toggleMember(uuid: string) {
        const idx = selectedMembers.indexOf(uuid);
        if (idx > -1) {
            selectedMembers.splice(idx, 1);
        } else {
            selectedMembers.push(uuid);
        }
        selectedMembers = selectedMembers;
    }

    function toggleCategory(categoryId: number) {
        const idx = selectedCategories.indexOf(categoryId);
        if (idx > -1) {
            selectedCategories.splice(idx, 1);
        } else {
            selectedCategories.push(categoryId);
        }
        selectedCategories = selectedCategories;
    }

    function clearFilters() {
        selectedLocation = null;
        selectedMembers = [];
        selectedCategories = [];
        memberSearchText = '';
        filterMode = 'OR';
    }

    function selectAllFiltered() {
        selectedMembers = [...filteredMembers.map((m) => m.uuid)];
    }

    function deselectAll() {
        selectedMembers = [];
    }

    // Get minimum date (today)
    let minDate = $derived.by(() => {
        const today = new Date();
        return today.toISOString().split('T')[0];
    });
</script>
<svelte:head>
    <title>Baza - SMS</title>
</svelte:head>

<div class="h-full bg-(--color-background-primary) px-4 py-8 md:px-8 md:py-12 mx-auto!">
    <div class="max-w-6xl mx-auto">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <!-- Left panel: Member selection -->
            <div class="flex flex-col gap-6">
                <div class="outline-2 outline outline-(--color-border) rounded-3xl p-8 bg-(--color-background-primary) p-4!">
                    <h2 class="text-2xl font-bold text-(--color-text-primary) mb-8">
                        Wybór odbiorców
                    </h2>

                    <!-- Filters -->
                    <div class="space-y-6 mb-8">
                        <!-- Location filter -->
                        <div class="flex flex-col gap-3">
                            <label class="text-sm font-semibold text-(--color-text-primary)">
                                Lokalizacja
                            </label>
                            <div class="bg-(--color-background-secondary) rounded-2xl p-4 w-fit">
                                <LocationSelect all={true} bind:location={selectedLocation} short={false} />
                            </div>
                        </div>

                        <!-- Search filter -->
                        <div class="flex flex-col gap-3">
                            <label for="member-search" class="text-sm font-semibold text-(--color-text-primary) p-1!">
                                Szukaj
                            </label>
                            <input
                                    id="member-search"
                                    type="text"
                                    bind:value={memberSearchText}
                                    placeholder="Imię, nazwisko, email..."
                                    class="bg-(--color-background-secondary) border-none rounded-2xl px-5! py-3! text-(--color-text-secondary) placeholder-gray-500 focus:outline-2 focus:outline-(--color-text-primary) transition"
                            />
                        </div>

                        <!-- Categories -->
                        <div class="flex flex-col gap-3">
                            <label class="text-sm font-semibold text-(--color-text-primary) px-1">
                                Kategorie
                            </label>
                            <div class="flex flex-wrap gap-3">
                                {#each categories as category (category.id)}
                                    <label class="flex items-center gap-3 bg-(--color-background-secondary) hover:bg-(--color-border) px-4! py-3! rounded-xl cursor-pointer transition">
                                        <input
                                                type="checkbox"
                                                checked={selectedCategories.includes(category.id)}
                                                onchange={() => toggleCategory(category.id)}
                                                class="accent-(--color-text-primary) cursor-pointer"
                                        />
                                        <span class="text-sm text-(--color-text-primary)">{category.shortname}</span>
                                    </label>
                                {/each}
                            </div>
                        </div>

                        <!-- Filter logic toggle -->
                        <div class="flex flex-col gap-3">
                            <label class="text-sm font-semibold text-(--color-text-primary) px-1!">
                                Logika filtrowania
                            </label>
                            <div class="flex gap-3">
                                <button
                                        class="flex-1 py-3! px-5! rounded-xl font-medium transition {filterMode === 'OR'
                                        ? 'bg-(--color-border) text-(--color-text-primary)'
                                        : 'bg-(--color-background-secondary) text-(--color-text-secondary) hover:bg-(--color-border)'}"
                                        onclick={() => (filterMode = 'OR')}
                                >
                                    LUB
                                </button>
                                <button
                                        class="flex-1 py-3 px-5 rounded-xl font-medium transition {filterMode === 'AND'
                                        ? 'bg-(--color-border) text-(--color-text-primary)'
                                        : 'bg-(--color-background-secondary) text-(--color-text-secondary) hover:bg-(--color-border)'}"
                                        onclick={() => (filterMode = 'AND')}
                                >
                                    I
                                </button>
                            </div>
                        </div>

                        <!-- Action buttons -->
                        <div class="flex gap-3 flex-wrap pt-6 gap-3! p-3! w-full">
                            <button onclick={clearFilters} class="text-sm font-medium bg-(--color-background-secondary) hover:bg-(--color-border) text-(--color-text-secondary) px-4 py-2.5 rounded-lg transition">
                                Wyczyść
                            </button>
                            {#if filteredMembers.length > 0}
                                <button onclick={selectAllFiltered} class="text-sm font-medium bg-(--color-background-secondary) hover:bg-(--color-border) text-(--color-text-secondary) px-4 py-2.5 rounded-lg transition">
                                    Zaznacz wszystkich
                                </button>
                                <button onclick={deselectAll} class="text-sm font-medium bg-(--color-background-secondary) hover:bg-(--color-border) text-(--color-text-secondary) px-4 py-2.5 rounded-lg transition">
                                    Odznacz wszystkich
                                </button>
                            {/if}
                        </div>
                    </div>

                    <!-- Selection counter -->
                    <div class="flex gap-8 bg-(--color-background-secondary) rounded-2xl w-fit p-2! mx-auto! mb-3! w-full">
                        <div class="flex flex-col gap-2 w-1/2">
                            <span class="text-xs font-semibold text-(--color-text-secondary) uppercase tracking-wider text-center! p-1">Zaznaczeni</span>
                            <span class="text-3xl font-bold text-(--color-text-primary) text-center">{selectedMembers.length}</span>
                        </div>
                        <div class="w-px bg-(--color-border)"></div>
                        <div class="flex flex-col gap-2 w-1/2">
                            <span class="text-xs font-semibold text-(--color-text-secondary) uppercase tracking-wider text-center! p-1">Dostępnych</span>
                            <span class="text-3xl font-bold text-(--color-text-primary) text-center">{filteredMembers.length}</span>
                        </div>
                    </div>

                    <!-- Members list -->
                    <div class="h-96 overflow-y-auto rounded-2xl outline-2 outline outline-(--color-border) bg-(--color-background-secondary)">
                        {#if filteredMembers.length === 0}
                            <div class="flex items-center justify-center h-full text-center p-8">
                                <span class="text-lg text-(--color-text-secondary)">Brak wyników</span>
                            </div>
                        {:else}
                            <div class="divide-y divide-(--color-border)">
                                {#each filteredMembers as member (member.uuid)}
                                    <label class="flex items-center gap-4 px-3! py-2! hover:bg-(--color-background-primary) cursor-pointer transition">
                                        <input
                                                type="checkbox"
                                                checked={selectedMembers.includes(member.uuid)}
                                                onchange={() => toggleMember(member.uuid)}
                                                class="accent-(--color-text-primary) cursor-pointer flex-shrink-0"
                                        />
                                        <div class="min-w-0 flex-1">
                                            <div class="font-medium text-(--color-text-primary) truncate">
                                                {member.name} {member.surname}
                                            </div>
                                            <div class="flex gap-3 mt-2 text-xs text-(--color-text-secondary)">
                                                <span class="px-3 py-1.5">
                                                    {member.location.shortname}
                                                </span>
                                                <span class="text-ellipsis overflow-hidden">{member.phoneNumber}</span>
                                            </div>
                                        </div>
                                    </label>
                                {/each}
                            </div>
                        {/if}
                    </div>
                </div>
            </div>

            <!-- Right panel: Message composition -->
            <div class="flex flex-col gap-6">
                <div class="outline-2 outline outline-(--color-border) rounded-3xl p-4! bg-(--color-background-primary)">
                    <h2 class="text-2xl font-bold text-(--color-text-primary) mb-8">
                        Wiadomość
                    </h2>

                    {#if form?.error}
                        <Error code={form?.error}>
                        </Error>
                    {/if}

                    <form action="?/sendSms" method="POST" use:enhance class="space-y-8">
                        <!-- Message text -->
                        <div class="flex flex-col gap-3">
                            <label for="message-text" class="text-sm font-semibold text-(--color-text-primary) px-1">
                                Treść wiadomości
                            </label>
                            <textarea
                                    id="message-text"
                                    name="messageText"
                                    bind:value={messageText}
                                    placeholder="Wpisz treść SMS-a..."
                                    required
                                    class="bg-(--color-background-secondary) border-none rounded-2xl px-5! py-4! text-(--color-text-secondary) placeholder-gray-500 resize-none focus:outline-2 focus:outline-(--color-text-primary) transition h-36"
                            ></textarea>
                            <div class="flex justify-between items-center text-xs px-1">
                                <span class="text-(--color-text-secondary)">
                                    {messageCharCount} / {maxMessageLength} znaków
                                </span>
                                {#if messageCharCount > maxMessageLength}
                                    <span class="text-red-500 font-medium">Przekroczony limit</span>
                                {/if}
                            </div>
                        </div>

                        <!-- Scheduling options -->
                        <div class="flex flex-col gap-4 w-fit">
                            <label class="text-sm font-semibold text-(--color-text-primary) px-1">
                                Kiedy wysłać
                            </label>
                            <div class="space-y-3 flex flex-row gap-2 my-2!">
                                <label class="flex items-center gap-4 p-4! bg-(--color-background-secondary) rounded-2xl cursor-pointer hover:bg-(--color-border) transition w-full">
                                    <input
                                            type="radio"
                                            name="scheduleType"
                                            value="now"
                                            bind:group={scheduleType}
                                            class="accent-(--color-text-primary) cursor-pointer"
                                    />
                                    <span class="text-sm font-medium text-(--color-text-primary)">Teraz</span>
                                </label>
                                <label class="flex items-center gap-4 p-4! bg-(--color-background-secondary) rounded-2xl cursor-pointer hover:bg-(--color-border) transition w-full">
                                    <input
                                            type="radio"
                                            name="scheduleType"
                                            value="scheduled"
                                            bind:group={scheduleType}
                                            class="accent-(--color-text-primary) cursor-pointer"
                                    />
                                    <span class="text-sm font-medium text-(--color-text-primary)">Zaplanuj na później</span>
                                </label>
                            </div>
                        </div>

                        {#if scheduleType === 'scheduled'}
                            <div class="grid grid-cols-2 gap-6 my-2! px-2">
                                <div class="flex flex-col gap-3">
                                    <label for="schedule-date" class="text-sm font-semibold text-(--color-text-primary) px-1">
                                        Data
                                    </label>
                                    <input
                                            id="schedule-date"
                                            type="date"
                                            name="scheduleDate"
                                            bind:value={scheduleDate}
                                            min={minDate}
                                            required
                                            class="bg-(--color-background-secondary) border-none rounded-2xl px-5 py-3 text-(--color-text-secondary) focus:outline-2 focus:outline-(--color-text-primary) transition"
                                    />
                                </div>

                                <div class="flex flex-col gap-3">
                                    <label for="schedule-time" class="text-sm font-semibold text-(--color-text-primary) px-1">
                                        Godzina
                                    </label>
                                    <input
                                            id="schedule-time"
                                            type="time"
                                            name="scheduleTime"
                                            bind:value={scheduleTime}
                                            required
                                            step="60"
                                            class="bg-(--color-background-secondary) border-none rounded-2xl px-5 py-3 text-(--color-text-secondary) focus:outline-2 focus:outline-(--color-text-primary) transition"
                                    />
                                </div>
                            </div>
                        {/if}

                        <!-- Hidden inputs -->
                        <input type="hidden" name="selectedMembers" value={JSON.stringify(selectedMembers)} />

                        <!-- Submit button -->
                        <button
                                type="submit"
                                disabled={selectedMembers.length === 0 || messageText.trim().length === 0}
                                class="w-full mt-10 py-4 px-8 rounded-2xl font-bold text-lg transition disabled:opacity-50 disabled:cursor-not-allowed {selectedMembers.length === 0 || messageText.trim().length === 0
                                ? 'bg-(--color-background-secondary) text-(--color-text-secondary)'
                                : 'bg-(--color-border) text-(--color-text-primary) hover:opacity-90'}"
                        >
                            <span>
                                {scheduleType === 'now' ? 'Wyślij SMS' : 'Zaplanuj SMS'}
                            </span>
                            <span class="ml-3 font-normal opacity-75">
                                ({selectedMembers.length})
                            </span>
                        </button>
                    </form>

                    {#if form?.success}
                        <div class="mt-3! bg-green-950/50 border border-green-700 text-green-200 px-6 py-4 rounded-2xl text-center">
                            Wiadomość {!form.scheduled ? 'wysłana' : 'zaplanowana'} do {form.processedCount} odbiorców
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    @import "tailwindcss";

    /* Tailwind doesn't support CSS variable syntax directly in class names,
       so we use them in inline styles or :global contexts */
    :global {
        input[type="date"],
        input[type="time"] {
            color-scheme: light;
        }

        input[type="date"]::-webkit-calendar-picker-indicator,
        input[type="time"]::-webkit-calendar-picker-indicator {
            filter: invert(0.8);
            cursor: pointer;
        }
    }

    /* Smooth scrollbar styling */
    ::-webkit-scrollbar {
        width: 6px;
    }

    ::-webkit-scrollbar-track {
        background: transparent;
    }

    ::-webkit-scrollbar-thumb {
        background: var(--text-secondary);
        border-radius: 3px;
    }

    ::-webkit-scrollbar-thumb:hover {
        background: var(--text-primary);
    }
</style>
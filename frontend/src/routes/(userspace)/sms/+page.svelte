<script lang="ts">
    import { enhance } from '$app/forms';
    import LocationSelect from '$lib/LocationSelect.svelte';

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
    <title>Wysyłanie SMS</title>
</svelte:head>

<div class="page-wrapper">
    <h1>Wysyłanie wiadomości SMS</h1>

    <div class="layout">
        <!-- Left panel: Member selection -->
        <div class="selection-panel">
            <h2>Wybór odbiorców</h2>

            <div class="filters-section">
                <div class="filter-group">
                    <label for="location-filter">Lokalizacja:</label>
                    <LocationSelect all={true} bind:location={selectedLocation} short={false} />
                </div>

                <div class="filter-group">
                    <label for="member-search">Szukaj:</label>
                    <input
                            id="member-search"
                            type="text"
                            bind:value={memberSearchText}
                            placeholder="Imię, nazwisko, email..."
                    />
                </div>

                <div class="filter-group">
                    <label>Kategorie:</label>
                    <div class="categories-selector">
                        {#each categories as category (category.id)}
                            <label class="category-checkbox">
                                <input
                                        type="checkbox"
                                        checked={selectedCategories.includes(category.id)}
                                        onchange={() => toggleCategory(category.id)}
                                />
                                <span>{category.shortname}</span>
                            </label>
                        {/each}
                    </div>
                </div>

                <div class="filter-group">
                    <label>Logika:</label>
                    <div class="mode-toggle">
                        <button
                                class="mode-btn {filterMode === 'OR' ? 'active' : ''}"
                                onclick={() => (filterMode = 'OR')}
                        >
                            LUB
                        </button>
                        <button
                                class="mode-btn {filterMode === 'AND' ? 'active' : ''}"
                                onclick={() => (filterMode = 'AND')}
                        >
                            I
                        </button>
                    </div>
                </div>

                <div class="filter-actions">
                    <button onclick={clearFilters} class="btn-small">Wyczyść</button>
                    {#if filteredMembers.length > 0}
                        <button onclick={selectAllFiltered} class="btn-small">Zaznacz wszystkich</button>
                        <button onclick={deselectAll} class="btn-small">Odznacz wszystkich</button>
                    {/if}
                </div>
            </div>

            <div class="selection-counter">
                <div class="counter-item">
                    <span class="label">Zaznaczeni:</span>
                    <span class="value">{selectedMembers.length}</span>
                </div>
                <div class="counter-item">
                    <span class="label">Dostępnych:</span>
                    <span class="value">{filteredMembers.length}</span>
                </div>
            </div>

            <div class="members-list-container">
                {#if filteredMembers.length === 0}
                    <div class="no-results">Brak wyników</div>
                {:else}
                    <div class="members-list">
                        {#each filteredMembers as member (member.uuid)}
                            <label class="member-item">
                                <input
                                        type="checkbox"
                                        checked={selectedMembers.includes(member.uuid)}
                                        onchange={() => toggleMember(member.uuid)}
                                />
                                <div class="item-content">
                                    <div class="item-name">{member.name} {member.surname}</div>
                                    <div class="item-meta">
                                        <span class="location">{member.location.shortname}</span>
                                        <span class="phone">{member.phoneNumber}</span>
                                    </div>
                                </div>
                            </label>
                        {/each}
                    </div>
                {/if}
            </div>
        </div>

        <!-- Right panel: Message composition -->
        <div class="message-panel">
            <h2>Wiadomość</h2>

            {#if form?.error}
                <div class="error-message">{form.error}</div>
            {/if}

            <form action="?/sendSms" method="POST" use:enhance class="sms-form">
                <!-- Message text -->
                <div class="form-group">
                    <label for="message-text">Treść wiadomości:</label>
                    <textarea
                            id="message-text"
                            name="messageText"
                            bind:value={messageText}
                            placeholder="Wpisz treść SMS-a..."
                            required
                    ></textarea>
                    <div class="char-count" class:over={messageCharCount > maxMessageLength}>
                        {messageCharCount} / {maxMessageLength} znaków
                    </div>
                </div>

                <!-- Scheduling options -->
                <div class="form-group">
                    <label>Kiedy wysłać:</label>
                    <div class="schedule-options">
                        <label class="radio-label">
                            <input
                                    type="radio"
                                    name="scheduleType"
                                    value="now"
                                    bind:group={scheduleType}
                            />
                            <span>Teraz</span>
                        </label>
                        <label class="radio-label">
                            <input
                                    type="radio"
                                    name="scheduleType"
                                    value="scheduled"
                                    bind:group={scheduleType}
                            />
                            <span>Zaplanuj</span>
                        </label>
                    </div>
                </div>

                {#if scheduleType === 'scheduled'}
                    <div class="form-group">
                        <label for="schedule-date">Data:</label>
                        <input
                                id="schedule-date"
                                type="date"
                                name="scheduleDate"
                                bind:value={scheduleDate}
                                min={minDate}
                                required
                        />
                    </div>

                    <div class="form-group">
                        <label for="schedule-time">Godzina:</label>
                        <input
                                id="schedule-time"
                                type="time"
                                name="scheduleTime"
                                bind:value={scheduleTime}
                                required
                        />
                    </div>
                {/if}

                <!-- Hidden inputs -->
                <input type="hidden" name="selectedMembers" value={JSON.stringify(selectedMembers)} />

                <!-- Submit button -->
                <button
                        type="submit"
                        class="btn-submit"
                        disabled={selectedMembers.length === 0 || messageText.trim().length === 0}
                >
					<span class="btn-label">
						{scheduleType === 'now' ? 'Wyślij SMS' : 'Zaplanuj SMS'}
					</span>
                    <span class="btn-count">({selectedMembers.length})</span>
                </button>
            </form>

            {#if form?.success}
                <div class="success-message">
                    Wiadomość {scheduleType === 'now' ? 'wysłana' : 'zaplanowana'} do {form.processedCount} odbiorców
                </div>
            {/if}
        </div>
    </div>
</div>

<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    .page-wrapper {
        max-width: 1400px;
        margin: 0 auto;
        padding: 20px;
    }

    h1 {
        text-align: center;
        margin-bottom: 30px;
        font-size: 2em;
        color: var(--color-text-primary);
    }

    .filters-section {
        outline: 2px solid var(--color-border);
        border-radius: 15px;
        padding: 20px;
        margin-bottom: 30px;
        background-color: var(--color-background-primary);
        display: flex;
        flex-direction: column;
        gap: 15px;
    }

    .filter-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
    }

    .filter-group label {
        font-weight: bold;
        color: var(--color-text-primary);
        font-size: 0.95em;
    }

    .filter-group input[type='text'] {
        background-color: var(--color-background-secondary);
        border: none;
        border-radius: 10px;
        padding: 10px 15px;
        color: var(--color-text-secondary);
        font-size: 0.95em;
    }

    .filter-group input[type='text']:focus {
        outline: 2px solid var(--color-text-primary);
    }

    .categories-selector {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: center;
    }

    .category-checkbox {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        background-color: var(--color-background-secondary);
        border-radius: 8px;
        cursor: pointer;
        user-select: none;
        transition: background-color 0.2s;
    }

    .category-checkbox input[type='checkbox'] {
        cursor: pointer;
        margin: 0;
        width: auto;
        accent-color: var(--color-text-primary);
    }

    .category-checkbox span {
        font-size: 0.9em;
    }

    .category-checkbox:has(input:checked) {
        background-color: var(--color-border);
    }

    .mode-toggle {
        display: flex;
        gap: 10px;
        margin-bottom: 8px;
    }

    .mode-btn {
        flex: 1;
        padding: 10px 15px;
        background-color: var(--color-background-secondary);
        border: 2px solid transparent;
        border-radius: 10px;
        color: var(--color-text-secondary);
        cursor: pointer;
        font-weight: 500;
        transition: all 0.2s;
    }

    .mode-btn.active {
        background-color: var(--color-border);
        border-color: var(--color-text-primary);
        color: var(--color-text-primary);
    }

    .mode-desc {
        font-size: 0.85em;
        color: var(--color-text-secondary);
        font-style: italic;
    }

    .filter-actions {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;
        padding-top: 10px;
        border-top: 1px solid var(--color-border);
    }

    .btn-primary,
    .btn-secondary,
    .btn-submit {
        padding: 10px 20px;
        border: none;
        border-radius: 10px;
        cursor: pointer;
        font-weight: 500;
        transition: all 0.2s;
        font-size: 0.9em;
    }

    .btn-primary {
        background-color: var(--color-border);
        color: var(--color-text-primary);
    }

    .btn-primary:hover {
        opacity: 0.9;
    }

    .btn-secondary {
        background-color: var(--color-background-secondary);
        color: var(--color-text-secondary);
    }

    .btn-secondary:hover {
        background-color: var(--color-border);
    }

    .error-message {
        background-color: rgba(255, 0, 0, 0.1);
        color: red;
        padding: 10px 15px;
        border-radius: 10px;
        margin-bottom: 20px;
        text-align: center;
    }

    .selection-info {
        display: flex;
        gap: 30px;
        margin-bottom: 20px;
        padding: 15px;
        background-color: var(--color-background-secondary);
        border-radius: 10px;
        font-weight: 500;
    }

    .selection-info strong {
        color: var(--color-text-primary);
    }

    .members-container {
        margin-bottom: 30px;
    }

    .no-results {
        text-align: center;
        font-size: 1.5em;
        color: var(--color-text-secondary);
        padding: 40px 20px;
    }

    .members-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        gap: 15px;
    }

    .member-card {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 15px;
        background-color: var(--color-background-secondary);
        border: 2px solid var(--color-border);
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.2s;
        user-select: none;
    }

    .member-card input[type='checkbox'] {
        margin-top: 4px;
        cursor: pointer;
        accent-color: var(--color-text-primary);
        flex-shrink: 0;
    }

    .member-card:has(input:checked) {
        background-color: var(--color-border);
        border-color: var(--color-text-primary);
    }

    .member-card:hover {
        border-color: var(--color-text-primary);
    }

    .card-content {
        flex: 1;
        min-width: 0;
    }

    .member-name {
        font-weight: bold;
        color: var(--color-text-primary);
        font-size: 1em;
        margin-bottom: 6px;
        word-break: break-word;
    }

    .member-details {
        display: flex;
        flex-direction: column;
        gap: 6px;
        margin-bottom: 8px;
    }

    .location-badge {
        display: inline-block;
        background-color: var(--color-background-primary);
        color: var(--color-text-secondary);
        padding: 3px 8px;
        border-radius: 6px;
        font-size: 0.8em;
        font-weight: 500;
        width: fit-content;
    }

    .categories-list {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
    }

    .category-tag {
        display: inline-block;
        background-color: var(--color-background-primary);
        color: var(--color-text-secondary);
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 0.75em;
        font-weight: 500;
    }

    .member-meta {
        display: flex;
        flex-direction: column;
        gap: 2px;
        font-size: 0.85em;
        color: var(--color-text-secondary);
        word-break: break-word;
    }

    .action-footer {
        position: sticky;
        bottom: 0;
        background-color: var(--color-background-primary);
        padding: 15px 20px;
        border-top: 2px solid var(--color-border);
        display: flex;
        justify-content: flex-end;
        z-index: 100;
    }

    .btn-submit {
        background-color: var(--color-border);
        color: var(--color-text-primary);
        padding: 12px 30px;
        font-size: 1em;
        font-weight: 600;
    }

    .btn-submit:hover {
        opacity: 0.9;
    }

    /* Desktop styles */
    @media screen and (width <= 1000px) {
        .page-wrapper {
            padding: 15px;
        }

        h1 {
            font-size: 1.5em;
            margin-bottom: 20px;
        }

        .filters-section {
            padding: 15px;
            gap: 12px;
            margin-bottom: 20px;
        }

        .filter-group {
            gap: 6px;
        }

        .filter-group label {
            font-size: 0.9em;
        }

        .categories-selector {
            gap: 6px;
        }

        .category-checkbox {
            padding: 5px 10px;
            gap: 4px;
        }

        .category-checkbox span {
            font-size: 0.85em;
        }

        .filter-actions {
            flex-direction: column;
        }

        .btn-primary,
        .btn-secondary {
            width: 100%;
        }

        .members-grid {
            grid-template-columns: 1fr;
        }

        .member-card {
            padding: 12px;
        }

        .action-footer {
            padding: 12px 15px;
        }

        .btn-submit {
            width: 100%;
            padding: 12px 20px;
        }
    }
</style>
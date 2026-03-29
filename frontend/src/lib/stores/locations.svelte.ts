import { writable } from 'svelte/store';

type Location = {
	id: number;
	name: string;
	shortName: string;
};

type LocationsStore = {
	data: Location[];
	loading: boolean;
	error: string | null;
	initialized: boolean;
};

function createLocationsStore() {
	const { subscribe, set, update } = writable<LocationsStore>({
		data: [],
		loading: false,
		error: null,
		initialized: false
	});

	return {
		subscribe,
		async load(force = false) {
			let current: LocationsStore;
			// Read current state
			const unsub = subscribe((s) => (current = s));
			unsub();

			if (current!.initialized && !force) return; // already loaded, skip
			if (current!.loading) return; // fetch in progress, skip

			update((s) => ({ ...s, loading: true, error: null }));

			try {
				const res = await fetch('/api/location/all');
				if (!res.ok) throw new Error('Failed to fetch locations');
				const data: Location[] = await res.json();
				set({ data, loading: false, error: null, initialized: true });
			} catch (e) {
				update((s) => ({ ...s, loading: false, error: (e as Error).message }));
			}
		},
		invalidate() {
			update((s) => ({ ...s, initialized: false }));
		}
	};
}

export const locations = createLocationsStore();

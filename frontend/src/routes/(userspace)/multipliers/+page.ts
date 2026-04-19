import type { PageLoad } from '../../../../.svelte-kit/types/src/routes/$types';

export const load: PageLoad = async (event) => {
    const multiplierRequest = await event.fetch("/api/multiplier", {method: "GET"});

	if (multiplierRequest) {
        const multipliers = await multiplierRequest.json();
		return { multipliers: multipliers };
	} else {
		return { error: true };
	}
};

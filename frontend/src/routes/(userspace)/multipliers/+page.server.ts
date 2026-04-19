import type {Actions} from "../../../../.svelte-kit/types/src/routes/(userspace)/db/$types";
import {serverFetch} from "$lib/api.ts";
import {fail} from "@sveltejs/kit";

export const actions: Actions = {
	addMultiplier: async ({ request, cookies, locals }) => {
		const data = await request.formData();

		const res = await serverFetch(
			'/api/multiplier',
			{
				method: 'POST',
				body: JSON.stringify({
					locationId: Number(data.get('locationId')),
					month: Number(data.get('month')),
					year: Number(data.get('year')),
					multiplier: Number(data.get('multiplier'))
				})
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? 'Nie udało się zapisać mnożnika'
			});
		}
	}
};
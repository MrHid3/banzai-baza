import type { Actions, PageLoad } from './$types';
import { fail } from '@sveltejs/kit';
import { serverFetch } from '$lib/api';

export const load: PageLoad = async (event) => {
	const res1 = await event.fetch(`/api/member`, { method: 'GET' });
	const categories = await event.fetch('/api/memberCategory', { method: 'GET' });

	if (res1.ok && categories.ok) {
		return {
			members: await res1.json(),
			categories: await categories.json()
		};
	} else {
		return { error: true };
	}
};

export const actions: Actions = {
	sendSms: async ({ request, cookies, locals }) => {
		const data = await request.formData();
		const selectedMembersJson = data.get('selectedMembers');

		if (!selectedMembersJson) {
			return fail(400, {
				error: 'Nie wybrano żadnych członków'
			});
		}

		let selectedMembers: string[] = [];
		try {
			selectedMembers = JSON.parse(selectedMembersJson as string);
		} catch {
			return fail(400, {
				error: 'Nieprawidłowe dane formularza'
			});
		}

		if (!Array.isArray(selectedMembers) || selectedMembers.length === 0) {
			return fail(400, {
				error: 'Nie wybrano żadnych członków'
			});
		}

		console.log(data.get("scheduleDate"), data.get("scheduleTime"))

		const res = await serverFetch(
			'/api/sms/send',
			{
				method: 'POST',
				body: JSON.stringify({
					memberUuids: selectedMembers,
					message: data.get("messageText"),
					scheduleDate: data.get("scheduleDate"),
					scheduleTime: data.get("scheduleTime")
				})
			},
			cookies,
			locals
		);

		if (!res.ok) {
			const body = await res.json().catch(() => ({}));
			return fail(res.status, {
				error: body.message ?? 'Nie udało się przetworzyć członków'
			});
		}

		return {
			success: true,
			processedCount: selectedMembers.length,
			selectedMembers,
			scheduled: data.get("scheduleDate") && data.get("scheduleTime")
		};
	}
};

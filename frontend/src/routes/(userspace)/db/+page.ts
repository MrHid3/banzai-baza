import { fetchWithAuth } from '$lib/fetchWithAuth.js';
import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load: PageLoad = async (event) => {

    const res = await fetchWithAuth(`/api/member/all`, {method: "GET"}, event.fetch)
    const json = await res.json();
    return { members: json};
};
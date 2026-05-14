import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";
import {PUBLIC_BACKEND_2} from "$env/static/public";

export const load: PageLoad = async (event) => {

    const res1 = await event.fetch(`/api/member`, {method: "GET"})
    const categories = await event.fetch("/api/memberCategory")

    if(res1.ok && categories.ok){
        return {
            members: await res1.json(),
            categories: await categories.json(),
        };
    } else {
        return { error : true };
    }
};
import type {PageLoad} from "../../../../.svelte-kit/types/src/routes/$types";

export const load : PageLoad = async({fetch, url}) => {

    const token = url.searchParams.get('token');

    const res = await fetch(`/api/auth/registration/validate?token=${token}`);

    if(res.ok){
        return { ok: true }
    }else{
        return { ok: false }
    }
}
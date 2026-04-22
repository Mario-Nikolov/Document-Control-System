import { useEffect, useState } from "react";

export function useForm(initialValues,submitCallBack){
    const [values,setValues] = useState(initialValues);

    const changeHendler = (e) => {
        const {name,value,files,type} = e.target;
        setValues(state => ({
            ...state,
            [name]: type ==='file' ? files[0] : value
        }))
    }

    const submitHendler = async (e) => {
        e.preventDefault();
        await submitCallBack(values);
        setValues(initialValues);
    }

    return {
        values,
        setValues,
        changeHendler,
        submitHendler,
    }
}
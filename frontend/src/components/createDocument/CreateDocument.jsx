import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useCreateDocument } from "../../hooks/useAuth";
import { useForm } from "../../hooks/useForm";

export default function CreateDocument({ onClose,onSave }) {
  const navigate = useNavigate();
  const { createDocumentHandler } = useCreateDocument();
  const [fileName, setFileName] = useState(null);
  const fileInputRef = useRef(null);

  const initialValues = {
    title: "",
    description: "",
    file: null,       // беше "content", сменено на "file"
    extension: "",
  };

  const onSubmit = async (values) => {
    await createDocumentHandler(values);
    if(onSave) await onSave();
  };


  // Имената съответстват на това, което useForm връща
  const { values, changeHendler, submitHendler, setValues } = useForm(
    initialValues,
    onSubmit,
  );

  const fileHandler = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const ext = file.name.split(".").pop();
    setFileName(file.name);

    setValues((prev) => ({   // setFormData -> setValues
      ...prev,
      file: file,
      extension: ext,
    }));
  };

  return (
    <form onSubmit={submitHendler}>  {/* закачен submitHendler */}
      <div className="nd-overlay" onClick={onClose}>
        <div className="nd-box" onClick={(e) => e.stopPropagation()}>
          <div className="nd-header">
            <h2>Нов документ</h2>
            <button type="button" className="nd-close" onClick={onClose}>✕</button>
          </div>

          <div className="nd-field">
            <label>ЗАГЛАВИЕ</label>
            <input
              name="title"
              type="text"
              placeholder="Име на документа"
              value={values.title}
              onChange={changeHendler}   
              autoFocus
            />
          </div>

          <div className="nd-field">
            <label>ОПИСАНИЕ</label>
            <textarea
              name="description"
              placeholder="Кратко описание"
              value={values.description}
              onChange={changeHendler}  
              onKeyDown={(e) => {
                if(e.key === "Enter" && !e.shiftKey){
                  e.preventDefault();
                  submitHendler(e);
                }
              }} 
              rows={3}
            />
          </div>

          <div className="nd-field">
            <label>ФАЙЛ</label>
            <div
              className="nd-file-zone"
              onClick={() => fileInputRef.current.click()}
            >
              <i className="fas fa-upload" />
              <span>{fileName ? fileName : "Избери файл..."}</span>
              {values.extension && (
                <span className="nd-ext-badge">.{values.extension}</span>
              )}
            </div>
            <input
              ref={fileInputRef}
              type="file"
              style={{ display: "none" }}
              onChange={fileHandler}
            />
          </div>

          <div className="nd-footer">
            <button type="button" className="nd-btn-cancel" onClick={onClose}>
              Отказ
            </button>
            <button
              type="submit"
              className={`nd-btn-create ${!values.title.trim() || !values.file ? "nd-btn-create--disabled" : ""}`}
              disabled={!values.title.trim() || !values.file}
            >
              Създай
            </button>
          </div>
        </div>
      </div>
    </form>
  );
}
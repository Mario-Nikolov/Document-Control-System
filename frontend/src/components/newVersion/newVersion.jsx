import { useState, useRef } from "react";
import { useCreateNewVersion } from "../../hooks/useAuth";

export default function NewVersionModal({ documentId, onClose, onSave }) {
  const [file, setFile] = useState(null);
  const [extension, setExtension] = useState("");
  const [changeSummary, setChangeSummary] = useState("");
  const [fileName, setFileName] = useState("");
  const [localError, setLocalError] = useState(null);

  const fileInputRef = useRef(null);
  const { createVersionHandler, loading, error } = useCreateNewVersion();

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) return;

    const ext = selectedFile.name.split(".").pop().toLowerCase();
    setFile(selectedFile);
    setExtension(ext);
    setFileName(selectedFile.name);
  };

  const handleSubmit = async () => {
    setLocalError(null);

    if (!file) {
      setLocalError("Моля, избери файл.");
      return;
    }
    if (!changeSummary.trim()) {
      setLocalError("Моля, въведи описание на промените.");
      return;
    }

    try {
      const result = await createVersionHandler(
        documentId,
        file,
        extension,
        changeSummary,
      );
      console.log(extension);
      if (onSave) onSave(result);
      onClose();
    } catch {
      // error идва от hook-а
    }
  };

  const displayError = localError || error;

  return (
    <div className="nv-overlay" onClick={onClose}>
      <div className="nv-box" onClick={(e) => e.stopPropagation()}>
        {/* Хедър */}
        <div className="nv-header">
          <h2>Нова версия</h2>
          <button className="nv-close" onClick={onClose}>
            ✕
          </button>
        </div>

        {/* Файл */}
        <div className="nv-field">
          <label>ФАЙЛ</label>
          <div
            className={`nv-file-zone ${file ? "nv-file-zone--selected" : ""}`}
            onClick={() => fileInputRef.current.click()}
          >
            <i className="fas fa-upload" />
            <span>{fileName || "Избери файл..."}</span>
            {extension && (
              <span className="nv-ext-badge">.{extension.toUpperCase()}</span>
            )}
          </div>
          <input
            ref={fileInputRef}
            type="file"
            style={{ display: "none" }}
            onChange={handleFileChange}
          />
        </div>

        {/* Описание на промените */}
        <div className="nv-field">
          <label>ОПИСАНИЕ НА ПРОМЕНИТЕ</label>
          <textarea
            placeholder="Кратко описание на промените в тази версия..."
            value={changeSummary}
            onChange={(e) => setChangeSummary(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                handleSubmit(e);
              }
            }}
            rows={4}
            disabled={loading}
          />
        </div>

        {/* Грешка */}
        {displayError && <div className="nv-error">{displayError}</div>}

        {/* Footer */}
        <div className="nv-footer">
          <button
            className="nv-btn-cancel"
            onClick={onClose}
            disabled={loading}
          >
            Отказ
          </button>
          <button
            className={`nv-btn-create ${loading ? "nv-btn-create--loading" : ""}`}
            onClick={handleSubmit}
            disabled={loading}
          >
            {loading ? "Качване..." : "Качи версия"}
          </button>
        </div>
      </div>
    </div>
  );
}

import { useNavigate, useParams } from "react-router-dom";
import { useGetLatestVersionDoc } from "../../hooks/useAuth";
import { useEffect, useMemo, useState } from "react";
import NewVersionModal from "../newVersion/newVersion";

export default function DocDetails() {
  const navigate = useNavigate();
  const { documentId } = useParams();
  const [document] = useGetLatestVersionDoc(documentId);
  const [txtContent, setTxtContent] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleSaveVersion = (result) => {
    console.log("Новата версия е качена:", result);
    if (typeof refetch === "function") refetch();
  };
  // Понеже extension е null се опитваме да разберем разширението от първите байтове

  const detectedExtension = useMemo(() => {
    if (!document?.content) return null;
    if (document.extension) return document.extension.toLowerCase();

    // Декодираме първите байтове за да разберем типа
    try {
      const decoded = atob(document.content.substring(0, 8));

      if (decoded.startsWith("%PDF")) return "pdf";
    } catch {}

    return "txt";
  }, [document]);

  const fileUrl = useMemo(() => {
    if (!document?.content || !detectedExtension) return null;

    const byteCharacters = atob(document.content);
    const byteNumbers = Array.from(byteCharacters, (c) => c.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);

    const mime =
      detectedExtension === "pdf"
        ? "application/pdf"
        : "text/plain;charset=utf-8";
    const blob = new Blob([byteArray], { type: mime });
    return URL.createObjectURL(blob);
  }, [document, detectedExtension]);

  useEffect(() => {
    if (!fileUrl || detectedExtension !== "txt") return;
    fetch(fileUrl)
      .then((r) => r.text())
      .then(setTxtContent);
  }, [fileUrl, detectedExtension]);

  useEffect(() => {
    return () => {
      if (fileUrl) URL.revokeObjectURL(fileUrl);
    };
  }, [fileUrl]);

  const renderFilePreview = () => {
    if (!document?.content) return <p>Зареждане...</p>;

    if (detectedExtension === "pdf") {
      return (
        <iframe
          src={fileUrl}
          title="PDF Preview"
          width="100%"
          height="600px"
          style={{ border: "none" }}
        />
      );
    }

    if (detectedExtension === "txt") {
      return (
        <pre style={{ whiteSpace: "pre-wrap", wordBreak: "break-word" }}>
          {txtContent}
        </pre>
      );
    }
    return <p>Неподдържан формат.</p>;
  };

  return (
    <div className="details-page-container">
      <div className="container">
        <header className="header">
          <div className="title-area">
            <a className="back-arrow" onClick={() => navigate("/")}>
              ←
            </a>
            <div>
              {/* <h4>
                Техническа спецификация <span className="version">v2</span>
              </h4>
              <p>Основна техническа документация за проекта</p> */}

              <h4>
                {document?.documentTitle}
                <span className="version"> v{document?.versionNumber}</span>
              </h4>
              <p>{document?.documentDescription}</p>
            </div>
          </div>
          <div className="header-right">
            <span>3 версии</span>
            <button
              className="btn-primary"
              onClick={() => setIsModalOpen(true)}
            >
              + Нова версия
            </button>
          </div>
        </header>
        <div className="layout">
          {/* ЛЯВА КОЛОНА */}
          <aside className="sidebar">
            <h3>ИСТОРИЯ НА ВЕРСИИТЕ</h3>
            <details className="version-item active" open="">
              <summary className="version-summary">
                <span className="arrow">&gt;</span>
                <div className="conection">
                  <img src="/images/local-network.png" alt="" />
                </div>
                <strong>v3</strong>
                {/* <strong>v{document.versionNumber}</strong> */}

                <span className="badge">Чернова</span>
                {/* <span className="badge">{document.status}</span> */}
              </summary>
              <div className="version-extra">
                <p>Мария Иванова · 10.03.2026 г.</p>
                {/* <p>{document.createdByUsername} · {new Date(document.createdAt).toLocaleDateString(
                            "bg-BG",
                            {
                              day: "2-digit",
                              month: "2-digit",
                              year: "numeric",
                            },
                          )} г.</p> */}
                <small>2 коментара</small>
              </div>
            </details>

            <details className="version-item active" open="">
              <summary className="version-summary">
                <span className="arrow">&gt;</span>
                <div className="conection">
                  <img src="/images/local-network.png" alt="" />
                </div>
                <strong>v2</strong>
                <span className="badge">Чернова</span>
              </summary>
              <div className="version-extra">
                <p>Мария Иванова · 90.03.2026 г.</p>
                <small>2 коментара</small>
              </div>
            </details>

            <details className="version-item active" open="">
              <summary className="version-summary">
                <span className="arrow">&gt;</span>
                <div className="conection">
                  <img src="/images/local-network.png" alt="" />
                </div>
                <strong>v1</strong>
                <span className="badge">Чернова</span>
              </summary>
              <div className="version-extra">
                <p>Мария Иванова · 8.03.2026 г.</p>
                <small>2 коментара</small>
              </div>
            </details>
          </aside>
          {/* ОСНОВНО СЪДЪРЖАНИЕ */}
          <main className="content">
            <div className="content-header">
              <div className="title">
                <h2>v{document?.versionNumber}</h2>
                {/* <span className="badge">Чернова</span> */}
                <span className="badge">{document?.status}</span>
              </div>
              <div className="actions">
                <button className="btn">Сравнение</button>
                <button className="btn success">✔ Одобри</button>
                <button className="btn danger">✖ Отхвърли</button>
              </div>
            </div>
            <div className="card">
              <h3>Съдържание на файла</h3>
              {renderFilePreview()}
            </div>

            {/* КОМЕНТАРИ */}
            <div className="comments">
              <h3>КОМЕНТАРИ (2)</h3>
              <div className="comment">
                <div className="comment-header">
                  <strong>Иван Петров</strong>
                  <span>15.03.2026 г.</span>
                </div>
                <p>bddgdgdddb</p>
              </div>
              <div className="comment">
                <div className="comment-header">
                  <strong>Иван Петров</strong>
                  <span>15.03.2026 г.</span>
                </div>
                <p>sbsbsbs</p>
              </div>
              <input
                className="comment-input"
                placeholder="Добави коментар..."
              />
            </div>
          </main>
        </div>
        {isModalOpen && (
          <NewVersionModal
            documentId={documentId}
            onClose={() => setIsModalOpen(false)}
            onSave={handleSaveVersion}
          />
        )}
      </div>
    </div>
  );
}

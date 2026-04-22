import { useNavigate, useParams } from "react-router-dom";
import {
  useCreateComment,
  useGetAllComments,
  useGetAllVersions,
  useGetLatestVersionDoc,
  useGetReview,
} from "../../hooks/useAuth";
import { useEffect, useMemo, useState, version } from "react";
import NewVersionModal from "../newVersion/newVersion";
import { useForm } from "../../hooks/useForm";
import { useAuthContext } from "../../context/AuthContext";
import { deleteDocument, getVersionById } from "../../api/auth-api";
import mammoth from "mammoth";

export default function DocDetails() {
  const navigate = useNavigate();
  const { roleName, userName } = useAuthContext();
  const { documentId } = useParams();
  const [txtContent, setTxtContent] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [reviewState, setReviewState] = useState(null);
  const [reviewComment, setReviewComment] = useState("");
  const [reviewLoading, setReviewLoading] = useState(false);
  const [selectedVersion, setSelectedVersion] = useState(null);
  const [docxContent, setDocxContent] = useState("");

  const [document, , refetchDocument] = useGetLatestVersionDoc(documentId);
  const { getReviewHandler } = useGetReview();
  const [reviews, , refetchVersions] = useGetAllVersions(documentId);
  const activeVersion = selectedVersion ?? document;
  const { comments, refreshComments } = useGetAllComments(activeVersion?.id);
  const { createCommentHandler } = useCreateComment();

  const { values, changeHendler, submitHendler } = useForm(
    { body: "" },
    async (formValues) => {
      if (!formValues.body.trim()) return;

      try {
        await createCommentHandler(activeVersion?.id, formValues.body);
        refreshComments();
      } catch (err) {
        console.error("Грешка при изпращане на коментар:", err);
      }
    },
  );

  const handleVersionClick = async (versionId) => {
    const result = await getVersionById(versionId);
    setSelectedVersion(result);
  };

  const handleSaveVersion = async (result) => {
    console.log("Новата версия е качена:", result);
    // if (typeof refetch === "function") refetch();
    await refetchVersions();
    await refetchDocument();
    setIsModalOpen(false);
  };

  const handleOpenReview = (decision) => {
    setReviewState(decision);
    setReviewComment("");
  };

  const handleReviewSubmit = async () => {
    if (!reviewState) return;
    setReviewLoading(true);
    try {
      await getReviewHandler(document?.id, reviewComment, reviewState);
      setReviewState(null);
      setReviewComment("");
      refetchDocument();
      refetchVersions();
    } catch (err) {
      console.error("Грешка при изпращане:", err);
    } finally {
      setReviewLoading(false);
    }
  };
  // Понеже extension е null се опитваме да разберем разширението от първите байтове

  const detectedExtension = useMemo(() => {
    if (!activeVersion?.content) return null;
    if (activeVersion.extension) return activeVersion.extension.toLowerCase();

    // Декодираме първите байтове за да разберем типа
    try {
      const decoded = atob(activeVersion.content.substring(0, 8));
      if (decoded.startsWith("%PDF")) return "pdf";
    } catch {}

    return "txt";
  }, [activeVersion]);

  const isTextBased = (ext) => {
    return [
      "txt",
      "jsx",
      "js",
      "ts",
      "tsx",
      "html",
      "css",
      "json",
      "xml",
      "md",
      "java",
      "py",
    ].includes(ext);
  };

  const fileUrl = useMemo(() => {
    if (!activeVersion?.content || !detectedExtension) return null;

    const byteCharacters = atob(activeVersion.content);
    const byteNumbers = Array.from(byteCharacters, (c) => c.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);

    const mime =
      detectedExtension === "pdf"
        ? "application/pdf"
        : "text/plain;charset=utf-8";
    const blob = new Blob([byteArray], { type: mime });
    return URL.createObjectURL(blob);
  }, [activeVersion, detectedExtension]);

  useEffect(() => {
    if (!fileUrl || !isTextBased(detectedExtension)) return;
    fetch(fileUrl)
      .then((r) => r.text())
      .then(setTxtContent);
  }, [fileUrl, detectedExtension]);

  useEffect(() => {
    return () => {
      if (fileUrl) URL.revokeObjectURL(fileUrl);
    };
  }, [fileUrl]);

  useEffect(() => {
    if (!activeVersion?.content || detectedExtension !== "docx") return;

    const byteCharacters = atob(activeVersion.content);
    const byteNumbers = Array.from(byteCharacters, (c) => c.charCodeAt(0));
    const arrayBuffer = new Uint8Array(byteNumbers).buffer;

    mammoth
      .convertToHtml({ arrayBuffer })
      .then((result) => setDocxContent(result.value))
      .catch((err) => {
        console.error("Грешка при четене на docx:", err);
        setDocxContent("<p>Грешка при зареждане на документа.</p>");
      });
  }, [activeVersion, detectedExtension]);

  const renderFilePreview = () => {
    if (!activeVersion?.content) return <p>Зареждане...</p>;

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

    if (detectedExtension === "docx") {
      return (
        <div
          style={{ lineHeight: "1.6", fontSize: "14px" }}
          dangerouslySetInnerHTML={{ __html: docxContent || "Зареждане..." }}
        />
      );
    }

    if (isTextBased(detectedExtension)) {
      return (
        <pre style={{ whiteSpace: "pre-wrap", wordBreak: "break-word" }}>
          {txtContent}
        </pre>
      );
    }
    return <p>Неподдържан формат.</p>;
  };

  const deleteHandler = async () => {
    if (!window.confirm("Наистина ли искате да изтриете този документ?"))
      return;
    try {
      console.log(documentId)
      await deleteDocument(documentId);
      navigate("/");
    } catch (err) {
      alert("Неуспешно изтриване. Моля, проверете правата си.");
    }
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
              <h4>
                {document?.documentTitle}
                <span className="version">
                  {" "}
                  v{activeVersion?.versionNumber}
                </span>
              </h4>
              <p>{document?.documentDescription}</p>
            </div>
          </div>
          <div className="header-right">
            <span>{reviews.length} версии</span>

            {roleName !== "READER" && (
              <button
                className="btn-primary"
                onClick={() => setIsModalOpen(true)}
              >
                + Нова версия
              </button>
            )}
          </div>
        </header>
        <div className="layout">
          <aside className="sidebar">
            <h3>ИСТОРИЯ НА ВЕРСИИТЕ</h3>
            {reviews.length > 0 ? (
              reviews.map((review) => (
                <details
                  key={review.id}
                  className={`version-item ${review.id === activeVersion?.id ? "active" : ""}`}
                  onClick={() => {
                    // e.preventDefault();
                    handleVersionClick(review.id);
                  }}
                >
                  <summary className="version-summary">
                    <span className="arrow">&gt;</span>
                    <div className="conection">
                      <img src="/images/local-network.png" alt="" />
                    </div>
                    <strong>v{review.versionNumber}</strong>

                    <span className="badge">{review.status}</span>
                  </summary>
                  <div className="version-extra">
                    <p>
                      {review.createdByUsername} ·{" "}
                      {new Date(review.createdAt).toLocaleDateString("bg-BG", {
                        day: "2-digit",
                        month: "2-digit",
                        year: "numeric",
                      })}{" "}
                      г.
                    </p>
                    <small>{comments.length} коментара</small>
                  </div>
                </details>
              ))
            ) : (
              <h3>No reviews yet!</h3>
            )}
          </aside>

          {/* ОСНОВНО СЪДЪРЖАНИЕ */}
          <main className="content">
            <div className="content-header">
              <div className="title">
                <h2>v{activeVersion?.versionNumber}</h2>
                <span className="badge">{activeVersion?.status}</span>
              </div>

              <div className="actions">
                {roleName !== "READER" &&
                  roleName !== "AUTHOR" &&
                  (roleName === "ADMIN" ||
                    userName !== document?.createdByUsername) &&
                  document?.status === "IN_REVIEW" && (
                    <>
                      <button
                        className="btn success"
                        onClick={() => handleOpenReview("APPROVED")}
                      >
                        ✔ Одобри
                      </button>
                      <button
                        className="btn danger"
                        onClick={() => handleOpenReview("REJECTED")}
                      >
                        ✖ Отхвърли
                      </button>
                    </>
                  )}

                {roleName === "ADMIN" &&
                  userName === document?.createdByUsername && (
                    <div>
                      <button className="btn danger" onClick={deleteHandler}>
                        ✖ Изтрий
                      </button>
                    </div>
                  )}
              </div>
            </div>

            {/* REVIEW ФОРМА */}
            {reviewState && (
              <div className="card review-form">
                <div className="review-form-header">
                  <span
                    className={`review-label ${reviewState === "APPROVED" ? "approved" : "rejected"}`}
                  >
                    {reviewState === "APPROVED"
                      ? "✔ Одобряване"
                      : "✖ Отхвърляне"}
                  </span>
                  <button className="btn" onClick={() => setReviewState(null)}>
                    ✕
                  </button>
                </div>
                <textarea
                  className="comment-input review-textarea"
                  placeholder="Напиши коментар..."
                  value={reviewComment}
                  onChange={(e) => setReviewComment(e.target.value)}
                  rows={3}
                />
                <div className="review-form-footer">
                  <button
                    className={`btn ${reviewState === "APPROVED" ? "success" : "danger"}`}
                    onClick={handleReviewSubmit}
                    disabled={reviewLoading}
                  >
                    {reviewLoading ? "Изпращане..." : "Потвърди"}
                  </button>
                </div>
              </div>
            )}

            <div className="card">
              <h3>Съдържание на файла</h3>
              {renderFilePreview()}
            </div>

            {/* КОМЕНТАРИ */}
            <div className="comments">
              <h3>КОМЕНТАРИ ({comments.length})</h3>
              <div className="comments-list">
                {comments.length > 0 ? (
                  comments.map((c) => (
                    <div className="comment" key={c.id}>
                      <div className="comment-header">
                        <strong>{c.commentedByUsername}</strong>
                        <span>
                          {new Date(c.createdAt).toLocaleDateString("bg-BG")} .
                        </span>
                      </div>
                      <p>{c.body}</p>
                    </div>
                  ))
                ) : (
                  <p>Все още няма коментари към тази версия.</p>
                )}
              </div>

              <form onSubmit={submitHendler}>
                <input
                  className="comment-input"
                  name="body" // Трябва да съвпада с ключа в useForm
                  value={values.body}
                  onChange={changeHendler}
                  placeholder="Добави коментар..."
                />
                <button type="submit" style={{ display: "none" }}>
                  Изпрати
                </button>
              </form>
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

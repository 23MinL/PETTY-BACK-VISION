let originalImages = [];
let postType = "QNA";

const postId = new URLSearchParams(location.search).get("id");

// 🔄 안전한 뒤로가기 함수
function goBack() {
  // 현재 페이지가 수정 페이지라면 상세 페이지로
  if (postId) {
    window.location.replace(`/posts/detail?id=${postId}`);
  } else {
    // 아니면 히스토리 back
    window.history.back();
  }
}

// 🔐 로그인 체크 함수 (쿠키 기반)
function isLoggedIn() {
  return true; // 실제 체크는 getCurrentUser()에서
}

// 🔐 현재 로그인 사용자 정보 가져오기 (쿠키 기반)
async function getCurrentUser() {
    try {
        const res = await fetch('/api/users/me', {
            credentials: 'include' // 쿠키를 포함해서 요청
        });

        if (res.ok) {
            return await res.json();
        } else if (res.status === 401) {
            return null; // 로그인 안됨
        }
    } catch (err) {
        console.error('사용자 정보 조회 실패:', err);
    }
    return null;
}

document.addEventListener("DOMContentLoaded", async () => {
  // 🔐 페이지 로드 시 권한 체크
  const currentUser = await getCurrentUser();
  if (!currentUser) {
      alert("로그인이 필요한 페이지입니다.");
      window.location.replace("/login");
      return;
  }

  // 🔐 게시글 작성자 본인인지 확인
  await checkPostOwnership();

  fetchPostForEdit();

  // HTML의 실제 ID와 맞춤
  const imageInput = document.getElementById("edit-qna-imageFiles");
  if (imageInput) {
    imageInput.addEventListener("change", handleImageUpload);
  }

  const form = document.getElementById("editQnaForm");
  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      // 🔐 폼 제출 시에도 권한 체크
      const currentUser = await getCurrentUser();
      if (!currentUser) {
          alert("로그인이 필요합니다.");
          location.href = "/login";
          return;
      }

      const payload = {
        title: document.getElementById("edit-qna-title").value,
        content: document.getElementById("edit-qna-content").value,
        petType: getRadioValue("edit-qna-petType") || "OTHER",
        postType: postType,
        isResolved: getIsResolvedValue(),
        images: originalImages
      };

      const res = await fetch(`/api/posts/${postId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: 'include', // 쿠키 포함
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        alert("수정 완료!");
        // 🔥 수정 완료 후 qna 리스트로 이동
        window.location.replace("/posts/qna");
      } else {
            const error = await res.text();
            alert("수정 실패: " + error);
      }
    });
  }

  // isResolved 토글 기능 추가
  const resolvedCheckbox = document.getElementById("isResolved");
  const resolvedLabel = document.getElementById("resolvedLabel");

  if (resolvedCheckbox && resolvedLabel) {
    resolvedCheckbox.addEventListener("change", () => {
      resolvedLabel.textContent = resolvedCheckbox.checked ? "해결완료" : "미해결";
    });
  }
});

// 🔐 게시글 작성자 본인인지 확인
async function checkPostOwnership() {
    try {
        const [postRes, userRes] = await Promise.all([
            fetch(`/api/posts/${postId}`),
            getCurrentUser()
        ]);

        if (!postRes.ok) {
            alert("게시글을 찾을 수 없습니다.");
            location.href = "/";
            return;
        }

        const post = await postRes.json();
        const currentUser = userRes;

        if (!currentUser) {
            alert("로그인이 필요합니다.");
            location.href = "/login";
            return;
        }

        // 🔐 작성자 본인이 아니면 접근 차단
        const isOwner = currentUser.username === post.userName;
        if (!isOwner) {
            alert("본인이 작성한 게시글만 수정할 수 있습니다.");
            window.location.replace(`/posts/detail?id=${postId}`);
            return;
        }

    } catch (err) {
        console.error('권한 확인 실패:', err);
        alert("권한 확인에 실패했습니다.");
        location.href = "/";
    }
}

async function fetchPostForEdit() {
  const res = await fetch(`/api/posts/${postId}`);
  const post = await res.json();

  document.getElementById("edit-qna-title").value = post.title;
  document.getElementById("edit-qna-content").value = post.content;

  const petTypeInputs = document.querySelectorAll('input[name="edit-qna-petType"]');
  petTypeInputs.forEach(input => {
    if (input.value === post.petType) input.checked = true;
  });

  const resolvedCheckbox = document.getElementById("isResolved");
  const resolvedLabel = document.getElementById("resolvedLabel");
  if (resolvedCheckbox && resolvedLabel) {
    resolvedCheckbox.checked = post.isResolved;
    resolvedLabel.textContent = post.isResolved ? "해결완료" : "미해결";
  }

  const previewBox = document.getElementById("edit-qna-imagePreview");
  if (previewBox) {
    (post.images || []).forEach((img, index) => {
      const imgWrapper = document.createElement("div");
      imgWrapper.style.display = "inline-block";
      imgWrapper.style.margin = "5px";
      imgWrapper.innerHTML = `
        <img src="${img.imageUrl}" data-url="${img.imageUrl}" style="max-width: 100px; border-radius: 6px; object-fit: cover;">
        <button type="button" onclick="removeImage('${img.imageUrl}')" style="display: block; margin-top: 5px;">삭제</button>
      `;
      previewBox.appendChild(imgWrapper);

      originalImages.push({
        id: img.id,
        imageUrl: img.imageUrl,
        ordering: img.ordering,
        isDeleted: false
      });
    });
  }
}

function getRadioValue(name) {
  const radios = document.querySelectorAll(`input[name="${name}"]`);
  for (const radio of radios) {
    if (radio.checked) return radio.value;
  }
  return null;
}

function getIsResolvedValue() {
  const checkbox = document.getElementById("isResolved");
  return checkbox ? checkbox.checked : false;
}

async function handleImageUpload(e) {
    // 🔐 이미지 업로드 시 로그인 체크
    const currentUser = await getCurrentUser();
    if (!currentUser) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }
  const files = Array.from(e.target.files);
  if (!files.length) return;

  const currentCount = originalImages.filter(img => !img.isDeleted).length;
  const maxCount = 5;
  if (currentCount >= maxCount) {
    alert("최대 5개의 이미지를 업로드할 수 있습니다.");
    return;
  }

  const availableSlots = maxCount - currentCount;
  const filesToUpload = files.slice(0, availableSlots);

  const formData = new FormData();
  for (const file of filesToUpload) {
    formData.append("files", file);
  }

  const res = await fetch('/api/images/upload/multi', {
    method: 'POST',
    credentials: 'include', // 쿠키 포함
    body: formData
  });

  if (!res.ok) {
    alert("이미지 업로드 실패");
    return;
  }

  const json = await res.json();
  const previewBox = document.getElementById("edit-qna-imagePreview");

  if (previewBox && json.images) {
    json.images.forEach((img) => {
      if (originalImages.some(existing => existing.imageUrl === img.imageUrl)) return;

      originalImages.push(img);

      const imgWrapper = document.createElement("div");
      imgWrapper.style.display = "inline-block";
      imgWrapper.style.margin = "5px";
      imgWrapper.innerHTML = `
        <img src="${img.imageUrl}" data-url="${img.imageUrl}" style="max-width: 100px; border-radius: 6px; object-fit: cover;">
        <button type="button" onclick="removeImage('${img.imageUrl}')" style="display: block; margin-top: 5px;">삭제</button>
      `;
      previewBox.appendChild(imgWrapper);
    });
  }
}

function removeImage(url) {
  const img = originalImages.find(img => img.imageUrl === url);
  if (img) {
    img.isDeleted = true;
  }

  const wrapper = document.querySelector(`img[data-url='${url}']`)?.parentElement;
  if (wrapper) wrapper.remove();
}
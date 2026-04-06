document.addEventListener('DOMContentLoaded', () => {
    const stage = document.getElementById('success-celebration');
    if (!stage) {
        return;
    }

    if (!document.getElementById('success-confetti-style')) {
        const style = document.createElement('style');
        style.id = 'success-confetti-style';
        style.textContent = `
            #success-celebration {
                position: fixed;
                inset: 0;
                pointer-events: none;
                overflow: hidden;
                z-index: 0;
            }
            .success-confetti-piece {
                position: absolute;
                top: -12vh;
                width: 10px;
                height: 18px;
                border-radius: 999px;
                opacity: 0;
                animation: success-confetti-fall var(--fall-duration) linear forwards;
                transform: translate3d(0, 0, 0) rotate(0deg);
                will-change: transform, opacity;
            }
            @keyframes success-confetti-fall {
                0% {
                    opacity: 0;
                    transform: translate3d(0, -5vh, 0) rotate(0deg);
                }
                12% {
                    opacity: 1;
                }
                100% {
                    opacity: 0;
                    transform: translate3d(var(--drift), 110vh, 0) rotate(var(--spin));
                }
            }
        `;
        document.head.appendChild(style);
    }

    const colors = ['#000000', '#1f1b17', '#6f5a45', '#d0b08a'];
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        return;
    }

    const spawnPiece = () => {
        const piece = document.createElement('span');
        piece.className = 'success-confetti-piece';
        piece.style.left = `${Math.random() * 100}%`;
        piece.style.background = colors[Math.floor(Math.random() * colors.length)];
        piece.style.width = `${8 + Math.random() * 6}px`;
        piece.style.height = `${12 + Math.random() * 10}px`;
        piece.style.setProperty('--drift', `${(Math.random() - 0.5) * 22}vw`);
        piece.style.setProperty('--spin', `${(Math.random() - 0.5) * 960}deg`);
        piece.style.setProperty('--fall-duration', `${2200 + Math.random() * 1200}ms`);
        stage.appendChild(piece);
        piece.addEventListener('animationend', () => piece.remove(), { once: true });
    };

    const durationMs = 2800;
    const startedAt = Date.now();
    const tick = () => {
        for (let index = 0; index < 6; index++) {
            spawnPiece();
        }
        if (Date.now() - startedAt < durationMs) {
            window.setTimeout(() => window.requestAnimationFrame(tick), 140);
        }
    };

    tick();
});

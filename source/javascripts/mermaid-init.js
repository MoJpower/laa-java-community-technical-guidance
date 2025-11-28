// Load Mermaid from CDN and transform ```mermaid``` code fences so they render.
(function () {
  function renderMermaid() {
    if (!window.mermaid) return;

    var MERMAID_PREFIX = /^(graph|flowchart|sequenceDiagram|erDiagram|classDiagram|stateDiagram|journey|gantt|mindmap|timeline)\b/i;
    var candidates = Array.prototype.slice.call(
      document.querySelectorAll('code.language-mermaid, pre.highlight > code, pre > code')
    );

    var mermaidBlocks = candidates.filter(function (codeBlock) {
      if (codeBlock.dataset.mermaidProcessed === 'true') return false;
      var text = codeBlock.textContent.trim();
      return MERMAID_PREFIX.test(text);
    });

    if (mermaidBlocks.length === 0) return;

    mermaidBlocks.forEach(function (codeBlock) {
      codeBlock.dataset.mermaidProcessed = 'true';
      var text = codeBlock.textContent;
      var pre = codeBlock.closest('pre');
      var wrapper = pre && pre.parentElement;

      var container = document.createElement('div');
      container.className = 'mermaid';
      container.textContent = text;

      if (wrapper && wrapper.classList.contains('highlight')) {
        wrapper.parentElement.replaceChild(container, wrapper);
      } else if (pre && pre.parentElement) {
        pre.parentElement.replaceChild(container, pre);
      } else if (codeBlock.parentElement) {
        codeBlock.parentElement.replaceChild(container, codeBlock);
      }
    });

    window.mermaid.initialize({ startOnLoad: false });
    window.mermaid.run({ querySelector: '.mermaid' });
  }

  function loadMermaid() {
    if (window.mermaid) {
      renderMermaid();
      return;
    }

    var script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js';
    script.defer = true;
    script.onload = renderMermaid;
    document.head.appendChild(script);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadMermaid);
  } else {
    loadMermaid();
  }
})();

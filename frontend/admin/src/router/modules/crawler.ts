import type { RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw = {
  path: "/crawler",
  redirect: "/crawler/article-query",
  name: "Crawler",
  meta: {
    title: "网页爬虫",
    icon: "Bug",
    rank: 100
  },
  children: [
    {
      path: "/crawler/article-query",
      name: "CrawlerArticleQuery",
      component: () => import("@/views/crawler/article-query.vue"),
      meta: {
        title: "法规文章查询",
        showParent: true
      }
    }
  ]
};

export default routes;

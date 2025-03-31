export default [
  {
    id: 1,
    name: '芋道集团',
    isLeaf: false,
    children: [
      {
        id: 11,
        parentId: 1,
        name: 'IT中心',
        isLeaf: false,
        children: [
          {
            id: 111,
            parentId: 11,
            name: '马青楷',
            post: 'CTO',
            avatar: '/static/images/avatar3.jpg',
            isLeaf: true,
          },
          {
            id: 112,
            parentId: 11,
            name: '王伟',
            post: '前端开发',
            avatar: '/static/images/avatar2.jpg',
            isLeaf: true,
          },
          {
            id: 113,
            parentId: 11,
            name: '王博',
            post: '技术主管',
            avatar: '/static/images/avatar1.jpg',
            isLeaf: true,
          },
        ],
      },
      {
        id: 12,
        parentId: 1,
        name: '行政部门',
        isLeaf: true,
      },
    ],
  },
  {
    id: 2,
    name: '中软集团',
    isLeaf: false,
    children: [
      {
        id: 21,
        parentId: 2,
        name: '中软IT',
        isLeaf: false,
        children: [
          {
            id: 211,
            parentId: 21,
            name: '马青楷',
            post: 'CTO',
            isLeaf: true,
            avatar: '/static/images/avatar3.jpg',
          },
        ],
      },
    ],
  },
]

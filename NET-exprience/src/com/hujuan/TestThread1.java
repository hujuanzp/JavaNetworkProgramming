package com.hujuan;

import javax.swing.*;

public class TestThread1 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    public static void createGUI() {
        JFrame jf = new JFrame("测试窗口");
        jf.setSize(300, 300);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        final JLabel label = new JLabel();
        label.setText("正在计算");
        panel.add(label);

        jf.setContentPane(panel);
        jf.setVisible(true);

        // 创建后台任务
        SwingWorker<String, Object> task = new SwingWorker<String, Object>() {
            @Override
            protected String doInBackground() throws Exception {
                // 此处处于 SwingWorker 线程池中
                // 延时 5 秒，模拟耗时操作
                Thread.sleep(5000);
                // 返回计算结果
                return "Hello";
            }

            @Override
            protected void done() {
                // 此方法将在后台任务完成后在事件调度线程中被回调
                String result = null;
                try {
                    // 获取计算结果
                    result = get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                label.setText("结算结果: " + result);
            }
        };

        // 启动任务
        task.execute();
    }

}

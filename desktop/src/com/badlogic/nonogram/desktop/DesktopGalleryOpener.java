package com.badlogic.nonogram.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.nonogram.GalleryOpener;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DesktopGalleryOpener implements GalleryOpener {
    final String[] imagePath = {""};
    @Override
    public void getGalleryImagePath() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JFileChooser chooser = new JFileChooser();
                FileFilter imageFilter = new FileNameExtensionFilter(
                        "Image files", ImageIO.getReaderFileSuffixes());
                chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png"));
                JFrame f = new JFrame();
                f.setVisible(true);
                f.toFront();
                f.setVisible(false);
                int res = chooser.showSaveDialog(f);
                f.dispose();
                if (res == JFileChooser.APPROVE_OPTION) {
                    String filePath = String.valueOf(chooser.getSelectedFile());
                    imagePath[0] = filePath;
                }
            }
        });

        t.start();
        t.join();
    }

    @Override
    public String getSelectedFilePath() {
        return imagePath[0];
    }
}

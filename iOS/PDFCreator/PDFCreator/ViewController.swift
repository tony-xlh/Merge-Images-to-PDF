//
//  ViewController.swift
//  PDFCreator
//
//  Created by 徐力航 on 2024/6/14.
//

import UIKit
import PhotosUI

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, PHPickerViewControllerDelegate {
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        for item in results {
            print(item.itemProvider)
            if (item.itemProvider.canLoadObject(ofClass: UIImage.self)) {
                item.itemProvider.loadObject(ofClass: UIImage.self) { image , error  in
                    if let error{
                        print(error)
                    }
                    if let selectedImage = image as? UIImage{
                        print(selectedImage.size)
                    }
                }
            }
        }
        picker.dismiss(animated: true)
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return 3
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int,
                        forComponent component: Int) -> String? {
        if row == 0 {
            return "Black & White"
        }else if row == 1 {
            return "Grayscale"
        }else{
            return "Color"
        }
                
    }
    
    @IBOutlet weak var selectImagesUIButton: UIButton!
    
    @IBOutlet weak var colorModeUIPickerView: UIPickerView!
    @IBOutlet weak var enableAutoCroppingUISwitch: UISwitch!
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        colorModeUIPickerView.dataSource = self
        colorModeUIPickerView.delegate = self
    }

    @IBAction func selectImagesUIButton_clicked(_ sender: Any) {
        print("clicked")

        var configuration = PHPickerConfiguration(photoLibrary: .shared())
        //0 - unlimited 1 - default
        configuration.selectionLimit = 0
        configuration.filter = .images
        let pickerViewController = PHPickerViewController(configuration: configuration)
        pickerViewController.delegate = self
        present(pickerViewController, animated: true)
    }
}

